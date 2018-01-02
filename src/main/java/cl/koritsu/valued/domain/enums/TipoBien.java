package cl.koritsu.valued.domain.enums;

public enum TipoBien {
	
	CASA("Casa"),
	CASA_DE_REPOSO("Casa de reposo"),
	DEPARTAMENTO("Departamento"),
	FUNDO("Fundo"),
	GALPON("Galpón"),
	PARCELA("Parcela"),
	COMERCIAL("Comercial"),
	EDIFICIO("Edificio"),
	INDUSTRIA("Industria"),
	LOCAL("Local"),
	LOCAL_COMERCIAL("Local Comercial"),
	LOTE("Lote"),
	LOTE_INDUSTRIAL("Lote Industrial"),
	OFICINA("Oficina"),
	OTRO("Otro"),
	OTROS_BIENES_URBANOS("Otros Bienes Urbanos"),
	PILOTO("Piloto"),
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
