package cl.koritsu.valued.domain.enums;

public enum EtapaTasacion {
	CREAR_SOLICITUD,
	AGENDAR_VISITA,
	CONFIRMAR_VISITA,
	CREAR_INCIDENCIA,
	INGRESAR_INFORMACION,
	VISAR,
	ENVIAR_A_CLIENTE,
	FACTURAR;
	
	@Override
	public String toString() {
		switch (this) {
		case CREAR_SOLICITUD:
			return "Crear Solicitud";
		case AGENDAR_VISITA:
			return "Agendar Visita";
		case CONFIRMAR_VISITA:
			return "Confirmar Visita";
		case CREAR_INCIDENCIA:
			return "Crear Incidencia";
		case INGRESAR_INFORMACION:
			return "Ingresar información de Tasación";
		case VISAR:
			return "Visar";
		case ENVIAR_A_CLIENTE:
			return "Enviar a Cliente";
		case FACTURAR:
			return "Facturar";
		}
		return super.toString();
	}
}
