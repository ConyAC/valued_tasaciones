package cl.koritsu.valued.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import cl.koritsu.valued.ValuedUI;
import cl.koritsu.valued.domain.Rol;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.Permiso;
import cl.koritsu.valued.repositories.RolRepository;
import cl.koritsu.valued.repositories.UsuarioRepository;

@Transactional(readOnly=true)
public class UsuarioServiceImpl implements UserDetailsService {
	static Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);
	
	@Autowired
	UsuarioRepository rep;
	
	@Autowired
	RolRepository repRol;
	
//	@PostConstruct
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#init()
	 */
	public void init(){
		//si no existe un usuario admin, lo crea
		String userName = "admin@admin.com";
		
		Usuario usuario = rep.findByEmail(userName);
		logger.error("usuario {} ",usuario);
		if( usuario == null ){
			logger.debug("usuario null");
			
			//CREA EL ROL UN ADMINISTRADOR CENTRAL
			Rol role = new Rol();
			role.setNombre("Super Administrador");
			Set<Permiso> perm = new HashSet<Permiso>();	
			perm.add(Permiso.TASAR_BIEN);
			perm.add(Permiso.VISAR_TASACION);
			repRol.save(role);
			logger.debug("guardando role "+role);
			
			// CREA EL USUARIO ADMIN
			String password = "123456";			
			usuario = new Usuario();
			usuario.setId(1L);
			usuario.setNombres("Joseph");
			usuario.setApellidoPaterno("O'Shea");
			usuario.setRol(role);
			usuario.setEmail(userName);
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);
			usuario.setContrasena(hashedPassword);
			usuario.setContrasena2(hashedPassword);
			rep.save(usuario);			
			logger.debug("guardando usuario "+usuario);
			
			
		}else{
			logger.debug("usuario distinto de null");
		}
		
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#loadUserByUsername(java.lang.String)
	 */
	@Override
	  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		//SOLO PARA TESTING CREA USUARIOS SI NO EXISTEN
		init();
		
		Usuario entityUser = null;
		//recupera el usuario desde base de datos
		try{
			entityUser = rep.findByEmail(userName);
		}catch(Exception e){
			String errorMessage = e.toString();
	        if (errorMessage != null) {
	            Notification.show("Error Inesperado", errorMessage, Type.WARNING_MESSAGE);
	        }
			logger.error("Error al ingresar al usuario.",e);
		}
		
		if( entityUser == null ){
			logger.debug("usuario con  userName "+userName+" no encontrado");
			throw new UsernameNotFoundException("Usuario o password incorrectas");
		}
		
		logger.debug("logiando usuario "+entityUser);
        List<GrantedAuthority> ll = new LinkedList<GrantedAuthority>(); 
		if(entityUser.getRol() != null){
			ll = (List<GrantedAuthority>) getAuthorities(entityUser.getRol().getPermisos());
		}

		boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        
        logger.debug("entityUser.getEmail() "+entityUser.getEmail()+" entityUser.getPassword() "+entityUser.getContrasena());

        return new User(entityUser.getEmail(),
        		entityUser.getContrasena(), 
                enabled, 
                accountNonExpired,
                credentialsNonExpired, 
                accountNonLocked,
                ll);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#saveUser(cl.magal.asistencia.entities.User)
	 */
	@Transactional
	public void saveUser(Usuario user) {
		if(user == null) throw new RuntimeException("Usuario no debe ser nulo");
		//nuevo usuario o edición de usuarios
		if(user.getId() == null || user.getContrasena() != null ){
			savePassword(user);
		}else{ //si es edición recupera la información anterior
			Usuario db = rep.findOne(user.getId());
			user.setContrasena(db.getContrasena());
		}
		
		//guardando usuario
		rep.save(user);
	}
	
	private void savePassword(Usuario usuario) {
		if(usuario == null) {
			throw new RuntimeException("Usuario no debe ser nulo");
		}
		//las passwords deben coincidir
		if(!usuario.getContrasena().equals(usuario.getContrasena2())){
			throw new RuntimeException("Los passwords deben coincidir");
		}
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(usuario.getContrasena());
		usuario.setContrasena(hashedPassword);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findUser(java.lang.Long)
	 */
	public Usuario findUser(Long id){
		return rep.findOne(id);
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findRawRoleUser(java.lang.Long)
	 */
	public Integer findRawRoleUser(Long id) {
		return (Integer) rep.findRawRoleUser(id);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#deleteUser(java.lang.Long)
	 */
	public void deleteUser(Long id){
		rep.delete(id);
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findAllUser(org.springframework.data.domain.Pageable)
	 */
	public Page<Usuario> findAllUser(Pageable page) {
		return rep.findAllNotDeteled(page);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#getAuthorities(java.util.Set)
	 */
	public Collection<GrantedAuthority> getAuthorities(Set<Permiso> permisos) {
        List<GrantedAuthority> authList = getGrantedAuthorities(getPermission(permisos));
        return authList;
    }
   
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#getPermission(java.util.Set)
	 */
	public List<String> getPermission(Set<Permiso> permisos) {

       List<String> result = new LinkedList<String>();
       for(Permiso permiso : permisos )
    	   result.add(permiso.name());
       return result;
	}
   
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	public static List<GrantedAuthority> getGrantedAuthorities(
            List<String> permissions) {
        List<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();

        for (String permission : permissions) {
        	logger.debug("permission id {}",permission);
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findUsuarioByUsername(java.lang.String)
	 */
	public Usuario findUsuarioByUsername(String username) {
		return rep.findByEmail(username);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#saveRole(cl.magal.asistencia.entities.Role)
	 */
	public void saveRole(Rol role) {
		repRol.save(role);
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#clear()
	 */
	public void clear() {
		rep.deleteAll();
		
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#getAllRole()
	 */
	public List<Rol> getAllRole() {
		return (List<Rol>) repRol.findAll();
	}
}
