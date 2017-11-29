package cl.koritsu.valued.domain;

import cl.koritsu.valued.domain.enums.ClaseBien;
import cl.koritsu.valued.domain.enums.TipoBien;

public class Bien {
	
	Long id;
	ClaseBien clase;
	TipoBien tipo;
	String numeroManzana;
	String numeroPredial;
	String direccion;
	int superficieTerreno;
	int superficiConstruida;
	String antecedentes;
	Comuna comuna;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ClaseBien getClase() {
		return clase;
	}
	public void setClase(ClaseBien clase) {
		this.clase = clase;
	}
	public TipoBien getTipo() {
		return tipo;
	}
	public void setTipo(TipoBien tipo) {
		this.tipo = tipo;
	}
	public String getNumeroManzana() {
		return numeroManzana;
	}
	public void setNumeroManzana(String numeroManzana) {
		this.numeroManzana = numeroManzana;
	}
	public String getNumeroPredial() {
		return numeroPredial;
	}
	public void setNumeroPredial(String numeroPredial) {
		this.numeroPredial = numeroPredial;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public int getSuperficieTerreno() {
		return superficieTerreno;
	}
	public void setSuperficieTerreno(int superficieTerreno) {
		this.superficieTerreno = superficieTerreno;
	}
	public int getSuperficiConstruida() {
		return superficiConstruida;
	}
	public void setSuperficiConstruida(int superficiConstruida) {
		this.superficiConstruida = superficiConstruida;
	}
	public String getAntecedentes() {
		return antecedentes;
	}
	public void setAntecedentes(String antecedentes) {
		this.antecedentes = antecedentes;
	}
	public Comuna getComuna() {
		return comuna;
	}
	public void setComuna(Comuna comuna) {
		this.comuna = comuna;
	}
	
}
