package cl.koritsu.valued.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.TipoInforme;
import cl.koritsu.valued.domain.TipoOperacion;
import cl.koritsu.valued.repositories.ComunaRepository;
import cl.koritsu.valued.repositories.RegionRepository;
import cl.koritsu.valued.repositories.TipoInformeRepository;
import cl.koritsu.valued.repositories.TipoOperacionRepository;

@Service
public class ValuedService {

	@Autowired
	RegionRepository regionRepo;
	@Autowired
	ComunaRepository comunaRepo;
	@Autowired
	TipoOperacionRepository tipoOperacionRepo;
	@Autowired
	TipoInformeRepository tipoInformeRepo;

	
	public List<Region> getRegiones() {
		return (List<Region>) regionRepo.findAll();
	}

	public List<Comuna> getComunaPorRegion(Region region) {
		return comunaRepo.findByRegion(region);
	}

	public List<TipoOperacion> getOperaciones() {
		return (List<TipoOperacion>) tipoOperacionRepo.findAll();
	}

	public List<TipoInforme> getTipoInformes() {
		return (List<TipoInforme>) tipoInformeRepo.findAll();
	}

}
