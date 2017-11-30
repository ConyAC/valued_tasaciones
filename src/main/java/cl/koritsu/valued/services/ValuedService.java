package cl.koritsu.valued.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Contacto;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.Solicitante;
import cl.koritsu.valued.domain.Sucursal;
import cl.koritsu.valued.domain.TipoInforme;
import cl.koritsu.valued.domain.TipoOperacion;
import cl.koritsu.valued.repositories.ClienteRepository;
import cl.koritsu.valued.repositories.ComunaRepository;
import cl.koritsu.valued.repositories.ContactoRepository;
import cl.koritsu.valued.repositories.RegionRepository;
import cl.koritsu.valued.repositories.SolicitanteRepository;
import cl.koritsu.valued.repositories.SucursalRepository;
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
	@Autowired
	ClienteRepository clienteRepo;
	@Autowired
	SucursalRepository sucursalRepo;
	@Autowired
	ContactoRepository ejecutivoRepo;
	@Autowired
	SolicitanteRepository solicitanteRepo;
	
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

	public List<Cliente> getClientes() {
		return (List<Cliente>) clienteRepo.findAll();
	}

	public List<Sucursal> getSucursalByCliente(Cliente cliente) {
		return (List<Sucursal>) sucursalRepo.findByCliente(cliente);
	}

	public List<Contacto> getEjecutivosByCliente(Cliente cliente) {
		return (List<Contacto>) ejecutivoRepo.findByCliente(cliente);
	}

	public List<Solicitante> getSolicitantesByCliente(Cliente cliente) {
		return (List<Solicitante>) solicitanteRepo.findByCliente(cliente);
	}

}
