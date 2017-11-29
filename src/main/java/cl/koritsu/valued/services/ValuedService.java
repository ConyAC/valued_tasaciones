package cl.koritsu.valued.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.repositories.ComunaRepository;
import cl.koritsu.valued.repositories.RegionRepository;

@Service
public class ValuedService {

	@Autowired
	RegionRepository regionRepo;
	@Autowired
	ComunaRepository comunaRepo;
	
	public List<Region> getRegiones() {
		return (List<Region>) regionRepo.findAll();
	}

	public List<Comuna> getComunaPorRegion(Region region) {
		return comunaRepo.findByRegion(region);
	}

}
