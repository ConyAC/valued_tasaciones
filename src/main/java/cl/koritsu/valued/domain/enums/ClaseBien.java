package cl.koritsu.valued.domain.enums;

public enum ClaseBien {
	
	INMUEBLE("Inmueble"),MUEBLE("Mueble"),
	BIENES_URBANO_HAB("Bienes Urbanos Habitacionales"),
	OTROS_BIENES_URBANO("Otros Bienes Urbanos"),
	BIEN_RAIZ_AGRICOLA("Bien Raíz Agrícola"),
	VEHICULO_MAQUINARIA_EQUIPOS("Vehículo, Maquinarias y Equipos");
	String label;
	ClaseBien(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
