package cl.koritsu.valued.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cl.koritsu.valued.domain.enums.EstadoSolicitud;

@Entity
@Table(name="solicitud_tasacion")
public class SolicitudTasacion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Temporal(TemporalType.DATE)
	Date fechaEncargo;
	@Temporal(TemporalType.DATE)
	Date fechaTasacion;
	@Temporal(TemporalType.DATE)
	Date fechaVisado;
	@Temporal(TemporalType.DATE)
	Date fechaEnvioCliente;
	
	float montoCompraEstimado;
	float montoTasacionPesos;
	float montoTasacionUF;
	
	String nombrePropietario = "";
	String rutPropietario = "";
	String telefonoPropietario = "";
	String emailPropietario = "";
	
	String nombreContacto = "";
	String telefonoFijoContacto = "";
	String telefonoMovilContacto = "";
	String emailContacto = "";
	
	String nombreContacto2 = "";
	String telefonoFijoContacto2 = "";
	String telefonoMovilContacto2 = "";
	String emailContacto2 = "";
	
	String numeroTasacionCliente = "";
	String numeroTasacion = "";
	boolean requiereTasador;
	
	@Enumerated(EnumType.STRING)
	EstadoSolicitud estado = EstadoSolicitud.CREADA;
	
	@JoinColumn(name="tipoInformeId")
	TipoInforme tipoInforme;
	@JoinColumn(name="tipoOperacionId")
	TipoOperacion tipoOperacion;
	
	@JoinColumn(name="sucursalId")
	Sucursal sucursal;
	@JoinColumn(name="solicitanteId")
	Solicitante solicitante;
	@JoinColumn(name="usuarioId")
	Usuario usuario;
	@JoinColumn(name="bienId")
	Bien bien;
	@JoinColumn(name="honorarioClienteId")
	HonorarioCliente honorarioCliente;
	@JoinColumn(name="clienteId")
	Cliente cliente;
	@JoinColumn(name="ejecutivoId")
	Contacto ejecutivo;
	@JoinColumn(name="tasadorId")
	Usuario tasador;
	
	
	String observaciones = "";
	float norteY;
	float esteX;
	
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
	public float getMontoCompraEstimado() {
		return montoCompraEstimado;
	}
	public void setMontoCompraEstimado(float montoCompraEstimado) {
		this.montoCompraEstimado = montoCompraEstimado;
	}
	public float getMontoTasacionPesos() {
		return montoTasacionPesos;
	}
	public void setMontoTasacionPesos(float montoTasacionPesos) {
		this.montoTasacionPesos = montoTasacionPesos;
	}
	public float getMontoTasacionUF() {
		return montoTasacionUF;
	}
	public void setMontoTasacionUF(float montoTasacionUF) {
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
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
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
	public float getNorteY() {
		return norteY;
	}
	public void setNorteY(float norteY) {
		this.norteY = norteY;
	}
	public float getEsteX() {
		return esteX;
	}
	public void setEsteX(float esteX) {
		this.esteX = esteX;
	}
	public HonorarioCliente getHonorarioCliente() {
		return honorarioCliente;
	}
	public void setHonorarioCliente(HonorarioCliente honorarioCliente) {
		this.honorarioCliente = honorarioCliente;
	}
	public String getEmailPropietario() {
		return emailPropietario;
	}
	public void setEmailPropietario(String emailPropietario) {
		this.emailPropietario = emailPropietario;
	}
	public String getTelefonoFijoContacto() {
		return telefonoFijoContacto;
	}
	public void setTelefonoFijoContacto(String telefonoFijoContacto) {
		this.telefonoFijoContacto = telefonoFijoContacto;
	}
	public String getTelefonoMovilContacto() {
		return telefonoMovilContacto;
	}
	public void setTelefonoMovilContacto(String telefonoMovilContacto) {
		this.telefonoMovilContacto = telefonoMovilContacto;
	}
	public String getTelefonoFijoContacto2() {
		return telefonoFijoContacto2;
	}
	public void setTelefonoFijoContacto2(String telefonoFijoContacto2) {
		this.telefonoFijoContacto2 = telefonoFijoContacto2;
	}
	public String getTelefonoMovilContacto2() {
		return telefonoMovilContacto2;
	}
	public void setTelefonoMovilContacto2(String telefonoMovilContacto2) {
		this.telefonoMovilContacto2 = telefonoMovilContacto2;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Contacto getEjecutivo() {
		return ejecutivo;
	}
	public void setEjecutivo(Contacto ejecutivo) {
		this.ejecutivo = ejecutivo;
	}
	public Usuario getTasador() {
		return tasador;
	}
	public void setTasador(Usuario tasador) {
		this.tasador = tasador;
	}
	
}