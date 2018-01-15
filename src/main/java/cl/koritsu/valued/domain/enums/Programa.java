package cl.koritsu.valued.domain.enums;

public enum Programa {
	
	DORMITORIO("Dormitorio"),
	BAÑO("Baño"),
	OTROS("Otro")
	;
	
	String label;
	Programa(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
