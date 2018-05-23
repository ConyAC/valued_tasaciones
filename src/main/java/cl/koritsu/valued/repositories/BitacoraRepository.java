package cl.koritsu.valued.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Bitacora;
import cl.koritsu.valued.domain.SolicitudTasacion;

public interface BitacoraRepository extends PagingAndSortingRepository<Bitacora, Long>, BitacoraRepositoryCustom {
	
	List<Bitacora> findByOrderByFechaInicioDesc();
	
	@Query("select max(b.fechaTermino) from Bitacora b where b.solicitudTasacion.id = ?1 ")
	Date findLastRow(Long id);

	@Query("select b from Bitacora b where b.solicitudTasacion = ?1 order by b.fechaInicio ASC")
	List<Bitacora> finddBitacoraBySol(SolicitudTasacion sol);
}
