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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((apellidoMaterno == null) ? 0 : apellidoMaterno.hashCode());
		result = prime * result
				+ ((apellidoPaterno == null) ? 0 : apellidoPaterno.hashCode());
		result = prime * result
				+ (int) (correlativoActual ^ (correlativoActual >>> 32));
		result = prime * result
				+ ((direccion == null) ? 0 : direccion.hashCode());
		result = prime * result
				+ ((factorKm == null) ? 0 : factorKm.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (multiRut ? 1231 : 1237);
		result = prime * result + ((nombres == null) ? 0 : nombres.hashCode());
		result = prime * result + ((prefijo == null) ? 0 : prefijo.hashCode());
		result = prime * result
				+ ((razonSocial == null) ? 0 : razonSocial.hashCode());
		result = prime * result + ((rut == null) ? 0 : rut.hashCode());
		result = prime * result
				+ ((telefonoFijo == null) ? 0 : telefonoFijo.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		result = prime * result
				+ ((tipoPersona == null) ? 0 : tipoPersona.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		if (apellidoMaterno == null) {
			if (other.apellidoMaterno != null)
				return false;
		} else if (!apellidoMaterno.equals(other.apellidoMaterno))
			return false;
		if (apellidoPaterno == null) {
			if (other.apellidoPaterno != null)
				return false;
		} else if (!apellidoPaterno.equals(other.apellidoPaterno))
			return false;
		if (correlativoActual != other.correlativoActual)
			return false;
		if (direccion == null) {
			if (other.direccion != null)
				return false;
		} else if (!direccion.equals(other.direccion))
			return false;
		if (factorKm == null) {
			if (other.factorKm != null)
				return false;
		} else if (!factorKm.equals(other.factorKm))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (multiRut != other.multiRut)
			return false;
		if (nombres == null) {
			if (other.nombres != null)
				return false;
		} else if (!nombres.equals(other.nombres))
			return false;
		if (prefijo == null) {
			if (other.prefijo != null)
				return false;
		} else if (!prefijo.equals(other.prefijo))
			return false;
		if (razonSocial == null) {
			if (other.razonSocial != null)
				return false;
		} else if (!razonSocial.equals(other.razonSocial))
			return false;
		if (rut == null) {
			if (other.rut != null)
				return false;
		} else if (!rut.equals(other.rut))
			return false;
		if (telefonoFijo == null) {
			if (other.telefonoFijo != null)
				return false;
		} else if (!telefonoFijo.equals(other.telefonoFijo))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		if (tipoPersona != other.tipoPersona)
			return false;
		return true;
	}  
}
