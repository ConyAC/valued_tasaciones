package cl.koritsu.valued.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.ConsolidadoCliente;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;

public interface SolicitudTasacionRepository extends PagingAndSortingRepository<SolicitudTasacion, Long>, SolicitudTasacionRepositoryCustom {
	
	SolicitudTasacion findFirstByNumeroTasacion(String nroEncargo);
	
	@Query("select s from SolicitudTasacion s join fetch s.tasador t where s.bien.comuna = ?1 order by s.fechaEncargo ASC")
	List<SolicitudTasacion> findByRegionAndComuna(Comuna comuna);

	@Query("select s from SolicitudTasacion s join fetch s.tasador t where t = ?1 order by s.fechaEncargo ASC")
	List<SolicitudTasacion> findByTasador(Usuario user);	

	@Query(value= "select s.* from solicitud_tasacion s join bien b on b.id = s.bienId"
			+ " where b.comunaId = :id and (acos(sin(:norteY) * sin(s.norteY) + cos(:norteY) * cos(s.norteY) * cos(s.esteX - (:esteX))) * 6371 <= 10 ) "
			+ " order by s.fechaEncargo ASC " , nativeQuery=true)
	List<SolicitudTasacion> findByCoordenadas(@Param("id") Long id, @Param("norteY") Float norteY, @Param("esteX") Float esteX);

	@Query("select s from SolicitudTasacion s join fetch s.tasador t where t = ?1 and s.estado not in (cl.koritsu.valued.domain.enums.EstadoSolicitud.FACTURA,cl.koritsu.valued.domain.enums.EstadoSolicitud.VISADA_CLIENTE)"
			+ " order by s.estado ASC, s.fechaTasacion ASC")
	List<SolicitudTasacion> findByTasadorEnProceso(Usuario user);

	@Query("select s from SolicitudTasacion s join fetch s.tasador t where s.estado not in (cl.koritsu.valued.domain.enums.EstadoSolicitud.FACTURA,cl.koritsu.valued.domain.enums.EstadoSolicitud.VISADA_CLIENTE)"
			+ " order by s.estado ASC, s.fechaTasacion ASC")
	List<SolicitudTasacion> findAllTasaciones();

	List<SolicitudTasacion> findByEstado(EstadoSolicitud tasada);

	@Query("select count(s) from SolicitudTasacion s where s.estado in ?1 ")
	int countByEstado(EstadoSolicitud... estado);

	@Query(value="select new cl.koritsu.valued.domain.ConsolidadoCliente(s.cliente, count(s.id)) from SolicitudTasacion s where s.estado in ?1 group by s.cliente order by count(s.id)  DESC")
	Page<ConsolidadoCliente> findTop10ByEstadoOrderByCount(Pageable page, EstadoSolicitud... tasada);

	@Query(value="select new cl.koritsu.valued.domain.ConsolidadoCliente(s.cliente, count(s.id)) from SolicitudTasacion s where extract(year from s.fechaEncargo) = extract(year from ?1) and extract(month from  s.fechaEncargo) = extract(month from ?1) group by s.cliente order by count(s.id) DESC ")
	Page<ConsolidadoCliente> findTop10MesByOrderByCount(Pageable pageRequest, Date date);

	
}
