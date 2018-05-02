package cl.koritsu.valued.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cl.koritsu.valued.domain.enums.EstadoFactura;

@Entity
@Table(name="factura")
public class Factura {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Temporal(TemporalType.DATE)
	Date fecha;
	
	float montoManual;
	float montoCalculado;
	
	String nombre = "";
	String numero = "";
	
	@Enumerated(EnumType.STRING)
	EstadoFactura estado = EstadoFactura.CREADA;
	
	@JoinColumn(name="usuarioId")
	Usuario usuario;
	@JoinColumn(name="clienteId")
	Cliente cliente;
	
//	@OneToMany(mappedBy="factura",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
//	List<SolicitudTasacion> solicitudesTable = new LinkedList<SolicitudTasacion>();
	
	//tabla intermedia entre factura y tasacion    
//	@OneToMany(mappedBy="factura",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true)
//    @CollectionTable(name="solicitud_tasacion", joinColumns = @JoinColumn(name = "facturaId"))
//    @JoinColumn(name="factura_solicitud")
    Set<SolicitudTasacion> solicitud = new HashSet<SolicitudTasacion>(); 
	
    
    @OneToMany
    @JoinTable(
            name="factura_solicitud_tasacion",
            joinColumns = @JoinColumn( name="factura_Id"),
            inverseJoinColumns = @JoinColumn( name="solicitud_Id")
    )
	public Set<SolicitudTasacion> getSolicitudes() {
		return solicitud;
	}
	public void setSolicitudes(Set<SolicitudTasacion> solicitud) {
		this.solicitud = solicitud;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public float getMontoManual() {
		return montoManual;
	}
	public void setMontoManual(float montoManual) {
		this.montoManual = montoManual;
	}
	public float getMontoCalculado() {
		return montoCalculado;
	}
	public void setMontoCalculado(float montoCalculado) {
		this.montoCalculado = montoCalculado;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
//	public List<SolicitudTasacion> getSolicitudesTable() {
//		return solicitudesTable;
//	}
//	public void setSolicitudesTable(List<SolicitudTasacion> solicitudesTable) {
//		this.solicitudesTable = solicitudesTable;
//	}
	public EstadoFactura getEstado() {
		return estado;
	}
	public void setEstado(EstadoFactura estado) {
		this.estado = estado;
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
}
