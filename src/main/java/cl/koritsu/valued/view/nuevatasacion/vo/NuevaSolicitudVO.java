package cl.koritsu.valued.view.nuevatasacion.vo;

import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Contacto;
import cl.koritsu.valued.domain.HonorarioCliente;
import cl.koritsu.valued.domain.Solicitante;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Sucursal;

public class NuevaSolicitudVO {
	
	Cliente cliente;
	Solicitante solicitante;
	Sucursal sucursal;
	Contacto ejecutivo;
	Bien bien;
	SolicitudTasacion solicitudTasacion;
	HonorarioCliente honorarioCliente;
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Solicitante getSolicitante() {
		return solicitante;
	}
	public void setSolicitante(Solicitante solicitante) {
		this.solicitante = solicitante;
	}
	public Sucursal getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}
	public Contacto getEjecutivo() {
		return ejecutivo;
	}
	public void setEjecutivo(Contacto ejecutivo) {
		this.ejecutivo = ejecutivo;
	}
	public Bien getBien() {
		return bien;
	}
	public void setBien(Bien bien) {
		this.bien = bien;
	}
	public SolicitudTasacion getSolicitudTasacion() {
		return solicitudTasacion;
	}
	public void setSolicitudTasacion(SolicitudTasacion solicitudTasacion) {
		this.solicitudTasacion = solicitudTasacion;
	}
	public HonorarioCliente getHonorarioCliente() {
		return honorarioCliente;
	}
	public void setHonorarioCliente(HonorarioCliente honorarioCliente) {
		this.honorarioCliente = honorarioCliente;
	}
	
}
