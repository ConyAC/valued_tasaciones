package cl.koritsu.valued.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import cl.koritsu.valued.domain.enums.TipoPersona;
import cl.koritsu.valued.domain.validator.RutDigit;

@Entity
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@RutDigit(message="El rut ingresado no es válido.")
    private String rut = "";
    private String nombres = "";
    private String apellidoPaterno = "";
    private String apellidoMaterno = "";
    private String razonSocial = "";
    private String telefonoFijo = "";
    @Enumerated(EnumType.STRING)
    private TipoPersona tipoPersona = TipoPersona.JURIDICA;
    private String factorKm = "";
    private String direccion = "";
    private String tipo = "";
    private boolean multiRut = false;
    
    private String prefijo = "";
    long correlativoActual;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRut() {
		return rut;
	}
	public void setRut(final String rut) {
		this.rut = rut;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(final String nombres) {
		this.nombres = nombres;
	}
	public String getApellidoPaterno() {
		return apellidoPaterno;
	}
	public void setApellidoPaterno(final String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}
	public String getApellidoMaterno() {
		return apellidoMaterno;
	}
	public void setApellidoMaterno(final String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(final String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getTelefonoFijo() {
		return telefonoFijo;
	}
	public void setTelefonoFijo(final String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}
	public TipoPersona getTipoPersona() {
		return tipoPersona;
	}
	public void setTipoPersona(final TipoPersona tipoPersona) {
		this.tipoPersona = tipoPersona;
	}
	public String getFactorKm() {
		return factorKm;
	}
	public void setFactorKm(final String factorKm) {
		this.factorKm = factorKm;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(final String direccion) {
		this.direccion = direccion;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(final String tipo) {
		this.tipo = tipo;
	}
	public boolean isMultiRut() {
		return multiRut;
	}
	public void setMultiRut(final boolean multiRut) {
		this.multiRut = multiRut;
	}
	
	public String getNombreCliente() {
		if(tipoPersona == TipoPersona.NATURAL )
			return getNombres() + " " + getApellidoPaterno() + " " + getApellidoMaterno();
		else 
			return getRazonSocial();
	}
	public String getPrefijo() {
		return prefijo;
	}
	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}
	public long getCorrelativoActual() {
		return correlativoActual;
	}
	public void setCorrelativoActual(long correlativoActual) {
		this.correlativoActual = correlativoActual;
	}
//	@Override
//	public String toString() {
//		return "Cliente [id=" + id + "]";
//	}
    
	public String getFullname(){
		if(razonSocial != null)
			return razonSocial;
		else
			return (nombres != null ? nombres : "") + " " + (apellidoPaterno != null ? apellidoPaterno : "");
    }
	@Override
	public String toString() {
		return "Cliente [id=" + id + ", rut=" + rut + ", nombres=" + nombres
				+ ", apellidoPaterno=" + apellidoPaterno + ", apellidoMaterno="
				+ apellidoMaterno + ", razonSocial=" + razonSocial
				+ ", telefonoFijo=" + telefonoFijo + ", tipoPersona="
				+ tipoPersona + ", factorKm=" + factorKm + ", direccion="
				+ direccion + ", tipo=" + tipo + ", multiRut=" + multiRut
				+ ", prefijo=" + prefijo + ", correlativoActual="
				+ correlativoActual + "]";
	}  
	
	
}
