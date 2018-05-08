package cl.koritsu.valued.domain;

public class ConsolidadoCliente {
	
	Cliente cliente;
	Integer cantidad;
	
	public ConsolidadoCliente(Cliente cliente, Integer cantidad) {
		this.cliente = cliente;
		this.cantidad = cantidad;
	}
	
	public ConsolidadoCliente(Cliente cliente, Long cantidad) {
		this.cliente = cliente;
		this.cantidad = cantidad != null ? cantidad.intValue() : 0;
	}
	
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
}
