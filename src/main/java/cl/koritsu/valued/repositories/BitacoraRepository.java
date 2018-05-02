package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Bitacora;

public interface BitacoraRepository extends PagingAndSortingRepository<Bitacora, Long>, BitacoraRepositoryCustom {
	
	List<Bitacora> findByOrderByFechaInicioDesc();
}
