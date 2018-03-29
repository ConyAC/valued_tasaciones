package cl.koritsu.valued.domain.enums;

public enum EstadoFactura {
	
	ANULADA("Anulada"),
	PAGADA("Pagada");
	
	String label;
	EstadoFactura(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}
}
