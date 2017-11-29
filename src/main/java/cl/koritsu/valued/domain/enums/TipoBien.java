package cl.koritsu.valued.domain.enums;

public enum TipoBien {
	
	CASA("Casa"),
	DEPARTAMENTO("Departamento"),
	FUNDO("Fundo"),
	GALPON("Galpón"),;
	
	String label;
	TipoBien(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
