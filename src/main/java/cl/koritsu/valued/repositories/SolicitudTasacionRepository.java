package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;

public interface SolicitudTasacionRepository extends PagingAndSortingRepository<SolicitudTasacion, Long> {
	
	
	@Query("select s from SolicitudTasacion s join fetch s.tasador t where s.bien.comuna = ?1 order by s.fechaEncargo ASC")
	List<SolicitudTasacion> findByRegionAndComuna(Comuna comuna);

	@Query("select s from SolicitudTasacion s join fetch s.tasador t where t = ?1 order by s.fechaEncargo ASC")
	List<SolicitudTasacion> findByTasador(Usuario user);	

	@Query(value= "select s.* from solicitud_tasacion s join bien b on b.id = s.bienId"
			+ " where b.comunaId = :id and s.id in "
			+ " (select st.id from solicitud_tasacion st where acos(sin(:norteY) * sin(st.norteY) + cos(:norteY) * cos(st.norteY) * cos(st.esteX - (:esteX))) * 6371 <= 10) "
			+ " order by s.fechaEncargo ASC " , nativeQuery=true)
	List<SolicitudTasacion> findByCoordenadas(@Param("id") Long id, @Param("norteY") Float norteY, @Param("esteX") Float esteX);

	@Query("select s from SolicitudTasacion s join fetch s.tasador t where t = ?1 and s.estado not in (cl.koritsu.valued.domain.enums.EstadoSolicitud.FACTURA,cl.koritsu.valued.domain.enums.EstadoSolicitud.VISADA_CLIENTE)"
			+ " order by s.estado ASC, s.fechaTasacion ASC")
	List<SolicitudTasacion> findByTasadorEnProceso(Usuario user);

	@Query("select s from SolicitudTasacion s join fetch s.tasador t where s.estado not in (cl.koritsu.valued.domain.enums.EstadoSolicitud.FACTURA,cl.koritsu.valued.domain.enums.EstadoSolicitud.VISADA_CLIENTE)"
			+ " order by s.estado ASC, s.fechaTasacion ASC")
	List<SolicitudTasacion> findAllTasaciones();
	
}
