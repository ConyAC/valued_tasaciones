package cl.koritsu.valued.domain.enums;

public enum EstadoUsuario {
	HABILITADO(1,"Habilitado"),
	DESHABILITADO(2,"Deshabilitado");
	
	int i;
	String description;
	private EstadoUsuario(int i,String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static EstadoUsuario getUserStatus(int i){
		for(EstadoUsuario e : EstadoUsuario.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("invalid Status");
	}
	
	@Override
	public String toString(){
		return description;
	}
	
}
