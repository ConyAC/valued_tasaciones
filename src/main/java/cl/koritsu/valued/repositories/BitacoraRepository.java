package cl.koritsu.valued.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Bitacora;

public interface BitacoraRepository extends PagingAndSortingRepository<Bitacora, Long>, BitacoraRepositoryCustom {
	
	List<Bitacora> findByOrderByFechaInicioDesc();
	
	@Query("select b.fechaTermino from Bitacora b where b.solicitudTasacion.id = ?1 ")
	Date findLastRow(Long id);
}
