package cl.koritsu.valued.domain.enums;

public enum TIPO_INFORME {
	
	TASACION("Tasación"),
	RE_TASACION("Re Tasación"),
	AVANCE_OBRA("Avance de Obra"),;
	
	String label;
	TIPO_INFORME(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
