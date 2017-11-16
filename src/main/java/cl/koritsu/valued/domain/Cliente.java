package cl.koritsu.valued.domain;

public final class Cliente {
	
    private String rut;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String razonSocial;
    private String telefonoFijo;
    private boolean tipoPersona;
    private String factorKm;
    private String direccion;
    private String tipo;
    private String multiRut;
    
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
	public boolean isTipoPersona() {
		return tipoPersona;
	}
	public void setTipoPersona(final boolean tipoPersona) {
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
	public String getMultiRut() {
		return multiRut;
	}
	public void setMultiRut(final String multiRut) {
		this.multiRut = multiRut;
	}
    
	
}
