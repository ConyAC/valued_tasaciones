package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import cl.koritsu.valued.domain.Rol;
import cl.koritsu.valued.domain.Usuario;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> {

	List<Usuario> findByTasadorTrue();
	
	@Query(value="SELECT u FROM Usuario u WHERE u.email = ?1 and u.estadoUsuario = cl.koritsu.valued.domain.enums.EstadoUsuario.HABILITADO ")
	Usuario findByEmail(String email); 
	
	@Query(value="SELECT u.rol FROM Usuario u WHERE u.id = :id " , nativeQuery=true)
	Integer findRawRoleUser(@Param("id") Long id);	
	
	@Query(value="SELECT u FROM Usuario u WHERE u.estadoUsuario = cl.koritsu.valued.domain.enums.EstadoUsuario.HABILITADO ")
	Page<Usuario> findAllNotDeteled(Pageable page);
	
	@Query(value="SELECT u FROM Usuario u WHERE u.rol = ?1 ")
	List<Usuario> findByRol(Rol rol); 
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Usuario u SET u.estadoUsuario = cl.koritsu.valued.domain.enums.EstadoUsuario.DESHABILITADO WHERE u = ?1 ")
	void deshabilitar(Usuario user);

}
