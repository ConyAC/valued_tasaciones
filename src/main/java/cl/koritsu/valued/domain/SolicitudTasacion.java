package cl.koritsu.valued.domain;

import java.util.Date;

import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.domain.enums.TipoInforme;
import cl.koritsu.valued.domain.enums.TipoOperacion;

public class SolicitudTasacion {
	
	Long id;
	Date fechaEncargo;
	Date fechaTasacion;
	Date fechaVisado;
	Date fechaEnvioCliente;
	
	int montoCompraEstimado;
	int montoTasacionPesos;
	int montoTasacionUF;
	String nombrePropietario;
	String rutPropietario;
	String telefonoPropietario;
	String nombreContacto;
	String telefonoContacto;
	String emailContacto;
	String nombreContacto2;
	String telefonoContacto2;
	String emailContacto2;
	String numeroTasacionCliente;
	String numeroTasacion;
	boolean requiereTasador;
	
	EstadoSolicitud estado;
	TipoInforme tipoInforme;
	TipoOperacion tipoOperacion;
	Sucursal sucursal;
	Solicitante solicitante;
	User usuario;
	Bien bien;
	HonorarioCliente honorarioCliente;
	
	String observaciones;
	int norteY;
	int esteX;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getFechaEncargo() {
		return fechaEncargo;
	}
	public void setFechaEncargo(Date fechaEncargo) {
		this.fechaEncargo = fechaEncargo;
	}
	public Date getFechaTasacion() {
		return fechaTasacion;
	}
	public void setFechaTasacion(Date fechaTasacion) {
		this.fechaTasacion = fechaTasacion;
	}
	public Date getFechaVisado() {
		return fechaVisado;
	}
	public void setFechaVisado(Date fechaVisado) {
		this.fechaVisado = fechaVisado;
	}
	public Date getFechaEnvioCliente() {
		return fechaEnvioCliente;
	}
	public void setFechaEnvioCliente(Date fechaEnvioCliente) {
		this.fechaEnvioCliente = fechaEnvioCliente;
	}
	public int getMontoCompraEstimado() {
		return montoCompraEstimado;
	}
	public void setMontoCompraEstimado(int montoCompraEstimado) {
		this.montoCompraEstimado = montoCompraEstimado;
	}
	public int getMontoTasacionPesos() {
		return montoTasacionPesos;
	}
	public void setMontoTasacionPesos(int montoTasacionPesos) {
		this.montoTasacionPesos = montoTasacionPesos;
	}
	public int getMontoTasacionUF() {
		return montoTasacionUF;
	}
	public void setMontoTasacionUF(int montoTasacionUF) {
		this.montoTasacionUF = montoTasacionUF;
	}
	public String getNombrePropietario() {
		return nombrePropietario;
	}
	public void setNombrePropietario(String nombrePropietario) {
		this.nombrePropietario = nombrePropietario;
	}
	public String getRutPropietario() {
		return rutPropietario;
	}
	public void setRutPropietario(String rutPropietario) {
		this.rutPropietario = rutPropietario;
	}
	public String getTelefonoPropietario() {
		return telefonoPropietario;
	}
	public void setTelefonoPropietario(String telefonoPropietario) {
		this.telefonoPropietario = telefonoPropietario;
	}
	public String getNombreContacto() {
		return nombreContacto;
	}
	public void setNombreContacto(String nombreContacto) {
		this.nombreContacto = nombreContacto;
	}
	public String getTelefonoContacto() {
		return telefonoContacto;
	}
	public void setTelefonoContacto(String telefonoContacto) {
		this.telefonoContacto = telefonoContacto;
	}
	public String getEmailContacto() {
		return emailContacto;
	}
	public void setEmailContacto(String emailContacto) {
		this.emailContacto = emailContacto;
	}
	public String getNombreContacto2() {
		return nombreContacto2;
	}
	public void setNombreContacto2(String nombreContacto2) {
		this.nombreContacto2 = nombreContacto2;
	}
	public String getTelefonoContacto2() {
		return telefonoContacto2;
	}
	public void setTelefonoContacto2(String telefonoContacto2) {
		this.telefonoContacto2 = telefonoContacto2;
	}
	public String getEmailContacto2() {
		return emailContacto2;
	}
	public void setEmailContacto2(String emailContacto2) {
		this.emailContacto2 = emailContacto2;
	}
	public String getNumeroTasacionCliente() {
		return numeroTasacionCliente;
	}
	public void setNumeroTasacionCliente(String numeroTasacionCliente) {
		this.numeroTasacionCliente = numeroTasacionCliente;
	}
	public String getNumeroTasacion() {
		return numeroTasacion;
	}
	public void setNumeroTasacion(String numeroTasacion) {
		this.numeroTasacion = numeroTasacion;
	}
	public boolean isRequiereTasador() {
		return requiereTasador;
	}
	public void setRequiereTasador(boolean requiereTasador) {
		this.requiereTasador = requiereTasador;
	}
	public EstadoSolicitud getEstado() {
		return estado;
	}
	public void setEstado(EstadoSolicitud estado) {
		this.estado = estado;
	}
	public TipoInforme getTipoInforme() {
		return tipoInforme;
	}
	public void setTipoInforme(TipoInforme tipoInforme) {
		this.tipoInforme = tipoInforme;
	}
	public TipoOperacion getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(TipoOperacion tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	public Sucursal getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}
	public Solicitante getSolicitante() {
		return solicitante;
	}
	public void setSolicitante(Solicitante solicitante) {
		this.solicitante = solicitante;
	}
	public User getUsuario() {
		return usuario;
	}
	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}
	public Bien getBien() {
		return bien;
	}
	public void setBien(Bien bien) {
		this.bien = bien;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public int getNorteY() {
		return norteY;
	}
	public void setNorteY(int norteY) {
		this.norteY = norteY;
	}
	public int getEsteX() {
		return esteX;
	}
	public void setEsteX(int esteX) {
		this.esteX = esteX;
	}
	public HonorarioCliente getHonorarioCliente() {
		return honorarioCliente;
	}
	public void setHonorarioCliente(HonorarioCliente honorarioCliente) {
		this.honorarioCliente = honorarioCliente;
	}
}
