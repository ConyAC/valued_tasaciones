package cl.koritsu.valued.domain.enums;

public enum Permisos {

	TASAR_BIEN(1, "Tasar Bien"),
	VISAR_TASACION(2,"Revisar Tasaciones");
	
	int i;
	String description;
	
	private Permisos(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Permisos getPermission(int i){
		for(Permisos e : Permisos.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Permission invalid");
	}

	@Override
	public String toString(){
		return description;
	}
}
