package cl.koritsu.valued.view.bitacora;

import java.util.Date;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EtapaTasacion;


public class BuscarBitacoraSolicitudVO {
	
	String nroTasacion;
	EtapaTasacion etapaTasacion;
	Usuario usuario;
	Cliente cliente;
	Date fechaInicio, fechaTermino;
	
	public String getNroTasacion() {
		return nroTasacion;
	}
	public void setNroTasacion(String nroTasacion) {
		this.nroTasacion = nroTasacion;
	}
	public EtapaTasacion getEtapaTasacion() {
		return etapaTasacion;
	}
	public void setEtapaTasacion(EtapaTasacion etapaTasacion) {
		this.etapaTasacion = etapaTasacion;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public Date getFechaTermino() {
		return fechaTermino;
	}
	public void setFechaTermino(Date fechaTermino) {
		this.fechaTermino = fechaTermino;
	}
}
