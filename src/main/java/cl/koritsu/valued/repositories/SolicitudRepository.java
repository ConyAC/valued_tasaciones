package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;

public interface SolicitudRepository extends PagingAndSortingRepository<SolicitudTasacion, Long> {

	SolicitudTasacion findFirstByNumeroTasacion(String nroEncargo);

	@Query(value="SELECT s FROM SolicitudTasacion s WHERE s.usuario = ?1 " )
	List<SolicitudTasacion> findByUser(Usuario user);

}
