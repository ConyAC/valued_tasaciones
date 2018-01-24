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

import cl.koritsu.valued.domain.enums.Adicional;

@Entity
@Table(name="OBRA_COMPLEMENTARIA")
public class ObraComplementaria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Enumerated(EnumType.STRING)
	Adicional adicional;
	long cantidadSuperficie;
	long valorTotalUF;
	@JoinColumn(name="bienId")
	@ManyToOne
	Bien bien;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Adicional getAdicional() {
		return adicional;
	}
	public void setAdicional(Adicional adicional) {
		this.adicional = adicional;
	}
	public long getCantidadSuperficie() {
		return cantidadSuperficie;
	}
	public void setCantidadSuperficie(long cantidadSuperficie) {
		this.cantidadSuperficie = cantidadSuperficie;
	}
	public long getValorTotalUF() {
		return valorTotalUF;
	}
	public void setValorTotalUF(long valorTotalUF) {
		this.valorTotalUF = valorTotalUF;
	}
	public Bien getBien() {
		return bien;
	}
	public void setBien(Bien bien) {
		this.bien = bien;
	}
	
}
