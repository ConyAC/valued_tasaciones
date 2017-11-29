package cl.koritsu.valued.domain.enums;

public enum EstadoTasacion {
	
	ENVIADA_CLIENTE("Enviada a Cliente"),
	FACTURADA("Facturada"),
	NUEVA_TASACION("Nueva Tasación"),
	VISITADA("Visitada"),
	VISADA("Visada"),;
	
	String label;
	EstadoTasacion(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
