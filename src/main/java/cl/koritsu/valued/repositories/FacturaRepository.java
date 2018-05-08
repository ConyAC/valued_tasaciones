package cl.koritsu.valued.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.koritsu.valued.domain.ConsolidadoCliente;
import cl.koritsu.valued.domain.Factura;

public interface FacturaRepository extends PagingAndSortingRepository<Factura, Long>, FacturaRepositoryCustom {
	
	List<Factura> findByOrderByFechaDesc();
	
	@Query(value="delete from factura_solicitud_tasacion where factura_Id = :id" , nativeQuery=true)
	@Modifying
	void deleteTasacionesDeFactura(@Param("id") Long id);

	@Query(value="select sum(f.montoManual) from Factura  f where extract(year from f.fecha) = extract(year from ?1) and extract(month from  f.fecha) = extract(month from ?1) ")
	Integer sumMes(Date date);

	@Query(value="select new cl.koritsu.valued.domain.ConsolidadoCliente(f.cliente, count(f.id)) from Factura  f where extract(year from f.fecha) = extract(year from ?1) and extract(month from  f.fecha) = extract(month from ?1) group by f.cliente order by count(f.id) DESC ")
	Page<ConsolidadoCliente> findTop10MesByOrderByCount(Pageable page, Date date);
	
}
