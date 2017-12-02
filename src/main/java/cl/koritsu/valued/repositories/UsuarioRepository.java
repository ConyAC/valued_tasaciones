package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Usuario;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> {

	List<Usuario> findByTasadorTrue();

}
