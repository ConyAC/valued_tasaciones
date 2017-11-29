package cl.koritsu.valued.domain.enums;

public enum ESTADO_TASACION {
	
	ENVIADA_CLIENTE("Enviada a Cliente"),
	FACTURADA("Facturada"),
	NUEVA_TASACION("Nueva Tasación"),
	VISITADA("Visitada"),
	VISADA("Visada"),;
	
	String label;
	ESTADO_TASACION(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
