package cl.koritsu.valued.domain;

public class Sucursal {
	
	Long id;
	String nombre;
	String direccion;
	Comuna comuna;
	Cliente cliente;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public Comuna getComuna() {
		return comuna;
	}
	public void setComuna(Comuna comuna) {
		this.comuna = comuna;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
}
