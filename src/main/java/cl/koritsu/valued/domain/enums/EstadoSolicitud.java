package cl.koritsu.valued.domain.enums;

public enum EstadoSolicitud {
	CREADA,TASADA,VISADA,VISADA_CLIENTE,FACTURA, VISITADA, AGENDADA,AGENDADA_CON_INCIDENCIA;
	
	@Override
	public String toString() {
		switch (this) {
		case CREADA:
			return "Asignada";
		case FACTURA:
			return "Facturada";
		case TASADA:
			return "Tasada";
		case VISADA:
			return "Visada";
		case VISADA_CLIENTE:
			return "Visada por Cliente";
		case VISITADA:
			return "Visitada";
		case AGENDADA_CON_INCIDENCIA:
			return "Visitada con incidencia";
		case AGENDADA:
			return "Agendada";

		}
		return super.toString();
	}
	/*
	public static EstadoSolicitud convertFromString(String name) {
		if( "Asignada".equals(name)) return CREADA;
		if( "Facturada".equals(name)) return FACTURA;
		if( "Visitada".equals(name)) return VISITADA;
		if( "Visada".equals(name)) return VISADA;
		if( "Visada por Cliente".equals(name)) return VISADA_CLIENTE;
		if( "Visada por Cliente".equals(name)) return VISADA_CLIENTE;
	case VISITADA:
		return "Visitada";
	case AGENDADA_CON_INCIDENCIA:
		return "Visitada con incidencia";
	case AGENDADA:
		return "Agendada";

	}*/
}
