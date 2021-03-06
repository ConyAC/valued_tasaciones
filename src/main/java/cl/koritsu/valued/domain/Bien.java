package cl.koritsu.valued.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import cl.koritsu.valued.domain.enums.ClaseBien;
import cl.koritsu.valued.domain.enums.TipoBien;

@Entity
public class Bien {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Enumerated(EnumType.STRING)
	ClaseBien clase;
	@Enumerated(EnumType.STRING)
	TipoBien tipo;
	
	String numeroManzana = "";
	String numeroPredial = "";
	String direccion = "";
	
	float superficieTerreno;
	float ufSuperficieTerreno;
	
	float superficieConstruida;
	float ufSuperficieConstruida;
	
	@Column(name="superficieTerraza")
	float superficieTerraza;
	float ufSuperficieTerraza;
	
	String antecedentes = "";
	@JoinColumn(name="comunaId")
	Comuna comuna;
	
	@OneToMany(mappedBy="bien",cascade=CascadeType.ALL)
	Set<ObraComplementaria> obrasComplementarias;
	
	@OneToMany(mappedBy="bien",cascade=CascadeType.ALL)
	Set<ProgramaBien> programas;
	
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
	public float getSuperficieTerreno() {
		return superficieTerreno;
	}
	public void setSuperficieTerreno(float superficieTerreno) {
		this.superficieTerreno = superficieTerreno;
	}
	public float getSuperficieConstruida() {
		return superficieConstruida;
	}
	public void setSuperficieConstruida(float superficieConstruida) {
		this.superficieConstruida = superficieConstruida;
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
	public float getSuperficieTerraza() {
		return superficieTerraza;
	}
	public void setSuperficieTerraza(float superficieTerraza) {
		this.superficieTerraza = superficieTerraza;
	}
	public Set<ObraComplementaria> getObrasComplementarias() {
		return obrasComplementarias;
	}
	public void setObrasComplementarias(Set<ObraComplementaria> obrasComplementarias) {
		this.obrasComplementarias = obrasComplementarias;
	}
	public Set<ProgramaBien> getProgramas() {
		return programas;
	}
	public void setProgramas(Set<ProgramaBien> programas) {
		this.programas = programas;
	}
	public float getUfSuperficieTerreno() {
		return ufSuperficieTerreno;
	}
	public void setUfSuperficieTerreno(float ufSuperficieTerreno) {
		this.ufSuperficieTerreno = ufSuperficieTerreno;
	}
	public float getUfSuperficieConstruida() {
		return ufSuperficieConstruida;
	}
	public void setUfSuperficieConstruida(float ufSuperficieConstruida) {
		this.ufSuperficieConstruida = ufSuperficieConstruida;
	}
	public float getUfSuperficieTerraza() {
		return ufSuperficieTerraza;
	}
	public void setUfSuperficieTerraza(float ufSuperficieTerraza) {
		this.ufSuperficieTerraza = ufSuperficieTerraza;
	}
	
}
