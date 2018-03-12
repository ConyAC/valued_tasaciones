package cl.koritsu.valued.view.busqueda;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;


public class BuscarSolicitudVO {

	String nroTasacion;
	EstadoSolicitud estado;
	Usuario tasador;
	Region region;
	Comuna comuna;
	
	public String getNroTasacion() {
		return nroTasacion;
	}
	public void setNroTasacion(String nroTasacion) {
		this.nroTasacion = nroTasacion;
	}
	public EstadoSolicitud getEstado() {
		return estado;
	}
	public void setEstado(EstadoSolicitud estado) {
		this.estado = estado;
	}
	public Usuario getTasador() {
		return tasador;
	}
	public void setTasador(Usuario tasador) {
		this.tasador = tasador;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public Comuna getComuna() {
		return comuna;
	}
	public void setComuna(Comuna comuna) {
		this.comuna = comuna;
	}
}
