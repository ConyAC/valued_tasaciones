package cl.koritsu.valued.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cl.koritsu.valued.domain.enums.Programa;

@Entity
@Table(name="PROGRAMA_BIEN")
public class ProgramaBien {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Enumerated(EnumType.STRING)
	Programa programa;
	long cantidadSuperficie;
	@JoinColumn(name="bienId")
	@ManyToOne
	Bien bien;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Programa getPrograma() {
		return programa;
	}
	public void setPrograma(Programa programa) {
		this.programa = programa;
	}
	public long getCantidadSuperficie() {
		return cantidadSuperficie;
	}
	public void setCantidadSuperficie(long cantidadSuperficie) {
		this.cantidadSuperficie = cantidadSuperficie;
	}
	public Bien getBien() {
		return bien;
	}
	public void setBien(Bien bien) {
		this.bien = bien;
	}
	
}
