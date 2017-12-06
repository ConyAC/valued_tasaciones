package cl.koritsu.valued.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import cl.koritsu.valued.domain.validator.RutDigit;

@Entity
public class Solicitante {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@RutDigit(message="El rut ingresado no es válido.")
	String rut;
	String nombres;
	String apellidoPaterno;
	String apellidoMaterno;
	String razonSocial;
	String telefonoFijo;
	String telefonoMovil;
	String email;
	@JoinColumn(name="clienteId")
	Cliente cliente;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRut() {
		return rut;
	}
	public void setRut(String rut) {
		this.rut = rut;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidoPaterno() {
		return apellidoPaterno;
	}
	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}
	public String getApellidoMaterno() {
		return apellidoMaterno;
	}
	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getTelefonoFijo() {
		return telefonoFijo;
	}
	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}
	public String getTelefonoMovil() {
		return telefonoMovil;
	}
	public void setTelefonoMovil(String telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public String getNombreCompleto() {
		StringBuilder sb = new StringBuilder();
		
			sb.append(getNombres()).append(" ");
		if(getApellidoPaterno() != null )
			sb.append(getApellidoPaterno()).append(" ");
		if(getApellidoMaterno() != null )
			sb.append(getApellidoMaterno()).append(" ");
		
		return sb.toString();
	}
	
}
