package cl.koritsu.valued.repositories;

import java.util.List;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.view.busqueda.BuscarSolicitudVO;


public interface SolicitudTasacionRepositoryCustom {

	List<SolicitudTasacion> findTasaciones(BuscarSolicitudVO vo);
	
}
