package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Factura;

public interface FacturaRepository extends PagingAndSortingRepository<Factura, Long> {
	
}
