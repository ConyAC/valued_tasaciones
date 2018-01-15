package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Usuario;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> {

	List<Usuario> findByTasadorTrue();
	
	@Query(value="SELECT u FROM Usuario u WHERE u.email = ?1 and u.habilitado = false ")
	Usuario findByEmail(String email); 
	

}
