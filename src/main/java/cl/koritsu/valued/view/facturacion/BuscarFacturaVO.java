package cl.koritsu.valued.view.facturacion;

import java.util.Date;

import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.enums.EstadoFactura;


public class BuscarFacturaVO {

	String nroFactura;
	EstadoFactura estadoFactura;
	Region region;
	Comuna comuna;
	Bien direccion;
	Cliente cliente;
	Date fechaDesde;
	Date fechaHasta;
	
	public String getNroFactura() {
		return nroFactura;
	}
	public void setNroFactura(String nroFactura) {
		this.nroFactura = nroFactura;
	}
	public EstadoFactura getEstadoFactura() {
		return estadoFactura;
	}
	public void setEstadoFactura(EstadoFactura estadoFactura) {
		this.estadoFactura = estadoFactura;
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
	public Bien getDireccion() {
		return direccion;
	}
	public void setDireccion(Bien direccion) {
		this.direccion = direccion;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Date getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}	
}
