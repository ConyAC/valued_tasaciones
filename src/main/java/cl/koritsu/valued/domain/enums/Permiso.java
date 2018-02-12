package cl.koritsu.valued.domain.enums;

public enum Permiso {

	TASAR_BIEN(1, "Tasar Bien"),
	VISAR_TASACION(2,"Revisar Tasaciones"),
	ADMINISTRACION(3,"Administración"), 
	INGRESAR_SOLICITUD(4,"Ingresar Solicitud"),
	FACTURAR(5,"Facturar"),
	ENVIAR_A_CLIENTE(6,"Enviar a Cliente"),
	VISUALIZAR_REPORTES(7,"Visualizar Reportes"),
	VISUALIZAR_TASACIONES(8,"Visualizar Todas las Tasaciones"),
	BUSCAR_TASACIONES(9,"Buscar Tasaciones")
	;
	
	int i;
	String description;
	
	private Permiso(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Permiso getPermiso(int i){
		for(Permiso e : Permiso.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Permiso Invalido");
	}

	@Override
	public String toString(){
		return description;
	}
}
