package cl.koritsu.valued.domain.enums;

public enum Adicional {
	
	BODEGA("Bodega"),
	ESTACIONAMIENTO("Estacionamiento"),
	OTROS("Otros")
	;
	
	String label;
	Adicional(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
