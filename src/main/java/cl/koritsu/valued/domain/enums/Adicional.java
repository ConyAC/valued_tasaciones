package cl.koritsu.valued.domain.enums;

public enum Adicional {
	
	BODEGA("Bodega"),
	ESTACIONAMIENTO("Estacionamiento"),
	ESTACIONAMIENTO_TANDER("Estacionamiento Tander"),
	ESTACIONAMIENTO_USO("Estacionamiento Uso y Goce"),
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
