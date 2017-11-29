package cl.koritsu.valued.domain.enums;

public enum ClaseBien {
	
	INMUEBLE("Inmueble"),MUEBLE("Mueble");
	String label;
	ClaseBien(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
