package cl.koritsu.valued.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="honorario_cliente")
public class HonorarioCliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	float factorKmUf;
	float kmTotal;
	float viatico;
	float montoHonorarioDesplazamientoUF;
	float montoHonorarioDesplazamientoPesos;
	float montoHonorarioUF;
	float montoHonorarioPesos;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public float getFactorKmUf() {
		return factorKmUf;
	}
	public void setFactorKmUf(float factorKmUf) {
		this.factorKmUf = factorKmUf;
	}
	public float getKmTotal() {
		return kmTotal;
	}
	public void setKmTotal(float kmTotal) {
		this.kmTotal = kmTotal;
	}
	public float getViatico() {
		return viatico;
	}
	public void setViatico(float viatico) {
		this.viatico = viatico;
	}
	public float getMontoHonorarioDesplazamientoUF() {
		return montoHonorarioDesplazamientoUF;
	}
	public void setMontoHonorarioDesplazamientoUF(float montoHonorarioDesplazamientoUF) {
		this.montoHonorarioDesplazamientoUF = montoHonorarioDesplazamientoUF;
	}
	public float getMontoHonorarioDesplazamientoPesos() {
		return montoHonorarioDesplazamientoPesos;
	}
	public void setMontoHonorarioDesplazamientoPesos(float montoHonorarioDesplazamientoPesos) {
		this.montoHonorarioDesplazamientoPesos = montoHonorarioDesplazamientoPesos;
	}
	public float getMontoHonorarioUF() {
		return montoHonorarioUF;
	}
	public void setMontoHonorarioUF(float montoHonorarioUF) {
		this.montoHonorarioUF = montoHonorarioUF;
	}
	public float getMontoHonorarioPesos() {
		return montoHonorarioPesos;
	}
	public void setMontoHonorarioPesos(float montoHonorarioPesos) {
		this.montoHonorarioPesos = montoHonorarioPesos;
	}
	

}
