package cl.koritsu.valued.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import cl.koritsu.valued.domain.enums.Adicional;

@Entity(name="OBRA_COMPLEMENTARIA")
public class ObraComplementaria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	Adicional adicional;
	long cantidadSuperficie;
	long valorTotalUF;
	long bienId;
	
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
	public long getBienId() {
		return bienId;
	}
	public void setBienId(long bienId) {
		this.bienId = bienId;
	}
	
}
