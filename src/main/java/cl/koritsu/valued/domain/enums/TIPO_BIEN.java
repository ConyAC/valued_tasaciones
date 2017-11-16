package cl.koritsu.valued.domain.enums;

public enum TIPO_BIEN {
	
	CASA("Casa"),
	DEPARTAMENTO("Departamento"),
	FUNDO("Fundo"),
	GALPON("Galpón"),;
	
	String label;
	TIPO_BIEN(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
