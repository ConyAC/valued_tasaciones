package cl.koritsu.valued.services;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.Cargo;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Contacto;
import cl.koritsu.valued.domain.HonorarioCliente;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.Solicitante;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Sucursal;
import cl.koritsu.valued.domain.TipoInforme;
import cl.koritsu.valued.domain.TipoOperacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.repositories.BienRepository;
import cl.koritsu.valued.repositories.CargoRepository;
import cl.koritsu.valued.repositories.ClienteRepository;
import cl.koritsu.valued.repositories.ComunaRepository;
import cl.koritsu.valued.repositories.ContactoRepository;
import cl.koritsu.valued.repositories.HonorarioClienteRepository;
import cl.koritsu.valued.repositories.RegionRepository;
import cl.koritsu.valued.repositories.SolicitanteRepository;
import cl.koritsu.valued.repositories.SolicitudRepository;
import cl.koritsu.valued.repositories.SolicitudTasacionRepository;
import cl.koritsu.valued.repositories.SucursalRepository;
import cl.koritsu.valued.repositories.TipoInformeRepository;
import cl.koritsu.valued.repositories.TipoOperacionRepository;
import cl.koritsu.valued.repositories.UsuarioRepository;

@Service
public class ValuedService {

	AtomicInteger ai = new AtomicInteger();
	
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
	@Autowired
	SolicitudRepository solicitudRepo;
	@Autowired
	HonorarioClienteRepository honorariosClienteRepo;
	@Autowired
	BienRepository bienRepo;
	@Autowired
	UsuarioRepository usuarioRepo;
	@Autowired
	SolicitudTasacionRepository solicitudTasacionRepo;
	@Autowired
	CargoRepository cargoRepo;
	
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
		return (List<Contacto>) ejecutivoRepo.findEjecutivosByCliente(cliente);
	}

	public List<Solicitante> getSolicitantesByCliente(Cliente cliente) {
		return (List<Solicitante>) solicitanteRepo.findByCliente(cliente);
	}

	public void saveSucursal(Sucursal bean) {
		sucursalRepo.save(bean);
	}

	public void saveCliente(Cliente bean) {
		clienteRepo.save(bean);
	}

	@Transactional
	public String saveSolicitud(SolicitudTasacion bean) {
		//guarda el bien
		Bien bien = bienRepo.save(bean.getBien());
		//guarda los honorarios
		HonorarioCliente honorarioCliente = honorariosClienteRepo.save(bean.getHonorarioCliente());
		
		//setea los bean controlados
		bean.setBien(bien);
		bean.setHonorarioCliente(honorarioCliente);
		//genera un nùmero de solicitud para valued
		String nroValued = bean.getCliente().getNombreCliente() +"-"+  ai.getAndIncrement()+"";
		bean.setNumeroTasacion(nroValued);
		//guarda la solicitud
		solicitudRepo.save(bean);
		
		return nroValued;
		
		
	}

	public List<Usuario> getTasadores() {
		return usuarioRepo.findByTasadorTrue();
	}

	public List<SolicitudTasacion> getTasaciones() {
		return (List<SolicitudTasacion>) solicitudTasacionRepo.findAll();
	}

	public void saveContacto(Contacto bean) {
		
		//busca el cargo dado
		Cargo cargo = cargoRepo.findOne(bean.getCargo().getId());
		
		bean.setCargo(cargo);
		
		ejecutivoRepo.save(bean);
		
	}

	public void saveSolicitante(Solicitante bean) {
		solicitanteRepo.save(bean);
	}

	public List<Cargo> getCargos() {
		return (List<Cargo>) cargoRepo.findAll();
	}
}
