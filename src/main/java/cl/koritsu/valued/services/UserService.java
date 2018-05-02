package cl.koritsu.valued.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cl.koritsu.valued.domain.Rol;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.Permiso;


public interface UserService {

	//@PostConstruct
	public abstract void init();

	/**
	 * TODO
	 */
	public abstract UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException;

	public abstract void saveUser(Usuario user);

	public abstract Usuario findUser(Long id);

	public abstract Integer findRawRoleUser(Long id);

	public abstract void deleteUser(Long id);

	public abstract List<Usuario> findAllUser();

	/**
	 * 
	 * @param permissions
	 * @return
	 */
	public abstract Collection<GrantedAuthority> getAuthorities(
			Set<Permiso> permissions);

	/**
	 * 
	 * @param permissions
	 * @return
	 */
	public abstract List<String> getPermission(Set<Permiso> permissions);

	/**
	 * Obtiene un usuario por su nombre de usuario
	 * @param username
	 * @return
	 */
	public abstract Usuario findUsuarioByUsername(
			String username);

	public abstract void saveRole(Rol role);

	/**
	 * Elimina todos los datos de la tabla USADO PRINCIPALMENTE POR LOS TEST 
	 */
	public abstract void clear();

	/**
	 * Devuelve una lista con todos los roles posibles
	 * @return
	 */
	public abstract List<Rol> getAllRole();

	public abstract boolean findUsersByRol(Rol rol);

}