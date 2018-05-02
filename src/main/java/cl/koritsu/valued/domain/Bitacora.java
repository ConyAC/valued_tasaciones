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

import cl.koritsu.valued.domain.enums.EtapaTasacion;

@Entity
@Table(name="bitacora")
public class Bitacora {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Temporal(TemporalType.DATE)
	Date fechaInicio;
	@Temporal(TemporalType.DATE)
	Date fechaTermino;
	
	@JoinColumn(name="usuarioId")
	Usuario usuario;

	@JoinColumn(name="solicitud_tasacionId")
	SolicitudTasacion solicitudTasacion;

	@Enumerated(EnumType.STRING)
	EtapaTasacion etapaTasacion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaTermino() {
		return fechaTermino;
	}

	public void setFechaTermino(Date fechaTermino) {
		this.fechaTermino = fechaTermino;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public SolicitudTasacion getSolicitudTasacion() {
		return solicitudTasacion;
	}

	public void setSolicitudTasacion(SolicitudTasacion solicitudTasacion) {
		this.solicitudTasacion = solicitudTasacion;
	}

	public EtapaTasacion getEtapa() {
		return etapaTasacion;
	}

	public void setEtapa(EtapaTasacion etapa) {
		this.etapaTasacion = etapa;
	}

	public EtapaTasacion getEtapaTasacion() {
		return etapaTasacion;
	}

	public void setEtapaTasacion(EtapaTasacion etapaTasacion) {
		this.etapaTasacion = etapaTasacion;
	}
}
