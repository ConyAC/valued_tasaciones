package cl.koritsu.valued.repositories;

import java.util.List;

import cl.koritsu.valued.domain.Factura;
import cl.koritsu.valued.view.busqueda.BuscarSolicitudVO;


public interface FacturaRepositoryCustom {

	List<Factura> findFacturas(BuscarSolicitudVO vo);
	
}
