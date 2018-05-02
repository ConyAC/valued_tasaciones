package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Factura;

public interface FacturaRepository extends PagingAndSortingRepository<Factura, Long>, FacturaRepositoryCustom {
	
	List<Factura> findByOrderByFechaDesc();
	
}
