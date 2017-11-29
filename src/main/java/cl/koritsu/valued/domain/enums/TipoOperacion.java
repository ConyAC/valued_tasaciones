package cl.koritsu.valued.domain.enums;

public enum TIPO_OPERACION {
	
	AVANCE_OBRA("Avance de Obra"),
	CREDITO_HIPOTECARIO("Crédito Hipotecario"),
	DACION_PAGO("Dación en Pago"),
	GARANTIA_GENERAL("Garantía General"),
	LEASEBACK_LEASING("Leaseback Leasing"),
	REMATE("Remate"),
	VENTA_ACTIVOS("Venta de Activos"),
	OTRO("Otro");
	
	String label;
	TIPO_OPERACION(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
