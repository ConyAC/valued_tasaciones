package cl.koritsu.valued.domain.enums;

public enum EstadoSolicitud {
	CREADA,TASADA,VISADA,VISADA_CLIENTE,FACTURA;
	
	@Override
	public String toString() {
		switch (this) {
		case CREADA:
			return "Asignada";
		case FACTURA:
			return "Facturada";
		case TASADA:
			return "Visitada";
		case VISADA:
			return "Visada";
		case VISADA_CLIENTE:
			return "Visada por Cliente";		
		}
		return super.toString();
	}
}
