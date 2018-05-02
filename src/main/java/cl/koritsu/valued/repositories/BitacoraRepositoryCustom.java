package cl.koritsu.valued.repositories;

import java.util.List;

import cl.koritsu.valued.domain.Bitacora;
import cl.koritsu.valued.view.bitacora.BuscarBitacoraSolicitudVO;


public interface BitacoraRepositoryCustom {

	List<Bitacora> findBitacora(BuscarBitacoraSolicitudVO vo);
	
}
