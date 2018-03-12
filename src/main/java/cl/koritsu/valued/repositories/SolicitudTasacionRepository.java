package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;

public interface SolicitudTasacionRepository extends PagingAndSortingRepository<SolicitudTasacion, Long> {
	
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
	
	//@Query("select s from SolicitudTasacion s where s.numeroTasacion = :#{#vo.nroTasacion} or s.estado = :#{#vo.estado} and s.bien.comuna = :#{#vo.comuna}")
	@Query("select s from SolicitudTasacion s where s.estado = ?1 or s.numeroTasacion = ?2 or s.tasador = ?3 or s.bien.comuna.region = ?4 or s.bien.comuna = ?5 order by  s.fechaEncargo DESC ")
//	@Query(value= "select st.* from solicitud_tasacion st join bien bi on bi.id = st.bienId join comuna com on com.id = bi.comunaId join region reg on reg.id = com.regionId "
//			+ " where st.estado like '%?1' and st.numeroTasacion like '%?2' and CONCAT(st.tasadorId, '') like '%?3' and CONCAT(reg.id, '') like '%?4' and CONCAT(com.id , '') like '%?5'"
//			+ " ORDER BY ?#{#pageable}", 
//			countQuery ="select * from solicitud_tasacion st join bien bi on bi.id = st.bienId join comuna com on com.id = bi.comunaId join region reg on reg.id = com.regionId "
//					+ " where st.estado like '%?1' and st.numeroTasacion like '%?2' and CONCAT(st.tasadorId, '') like '%?3' and CONCAT(reg.id, '') like '%?4' and CONCAT(com.id , '') like '%?5'"
//					+ " ORDER BY ?#{#pageable}", nativeQuery = true)
//	
	Page<SolicitudTasacion> findTasaciones(EstadoSolicitud estado, String nroTasacion, Usuario tasador,Region region, Comuna comuna,Pageable pageable);
	
}
