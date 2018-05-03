package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.koritsu.valued.domain.Factura;

public interface FacturaRepository extends PagingAndSortingRepository<Factura, Long>, FacturaRepositoryCustom {
	
	List<Factura> findByOrderByFechaDesc();
	
	@Query(value="delete from factura_solicitud_tasacion where factura_Id = :id" , nativeQuery=true)
	@Modifying
	void deleteTasacionesDeFactura(@Param("id") Long id);
	
}
