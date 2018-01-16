package cl.koritsu.valued.domain.enums;

public enum Permiso {

	TASAR_BIEN(1, "Tasar Bien"),
	VISAR_TASACION(2,"Revisar Tasaciones")
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
