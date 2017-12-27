package cl.koritsu.valued.domain.enums;

public enum TipoBien {
	
	CASA("Casa"),
	DEPARTAMENTO("Departamento"),
	FUNDO("Fundo"),
	GALPON("Galpón"),
	PARCELA("Parcela"),
	COMERCIAL("Comercial"),
	EDIFICIO("Edificio"),
	LOCAL("Local"),
	LOCAL_COMERCIAL("Local Comercial"),
	LOTE("Lote"),
	OFICINA("Oficina"),
	OTRO("Otro"),
	OTROS_BIENES_URBANOS("Otros Bienes Urbanos"),
	SITIO("Sitio"),
	TERRENO("Terreno"),
	TERRENO_INDUSTRIAL("Terreno Industrial"),
	VARIOS("Varios")
	;
	
	String label;
	TipoBien(String label){
		this.label = label;
	}
	
	public String toString() {
		return label;
	}

}
