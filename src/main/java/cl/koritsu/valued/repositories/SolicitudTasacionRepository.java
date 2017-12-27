package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.SolicitudTasacion;

public interface SolicitudTasacionRepository extends PagingAndSortingRepository<SolicitudTasacion, Long> {
	
	
	@Query("select s from SolicitudTasacion s where s.bien.comuna = ?1 ")
	List<SolicitudTasacion> findByRegionAndComuna(Comuna comuna);

}
