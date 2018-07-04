package cl.koritsu.valued.view.utils;

import java.io.IOException;
import java.util.List;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.services.ValuedService;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderComponent;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderStatus;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;

public class MapaTasacion {

    GoogleMap googleMap;
    ValuedService service;
    
    public MapaTasacion(ValuedService servicio, GoogleMap googleMap) {
		init(servicio, googleMap);
	}
    
    public void init(ValuedService servicio, GoogleMap googleMap) {   	
        this.service = servicio; 
        this.googleMap = googleMap;

    }
        
    public LatLon recuperarCoordenadas(SolicitudTasacion sol) {
		Geocoder geo = new Geocoder();
		LatLon coord = new LatLon();
		double lat = 0;
		double lon = 0;
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(sol.getBien().getDireccion()+" "+sol.getBien().getNumeroManzana()+" "+sol.getBien().getComuna().getNombre()).getGeocoderRequest();
		geocoderRequest.addComponent(GeocoderComponent.COUNTRY, "cl");
			try {
				GeocodeResponse geocoderResponse = geo.geocode(geocoderRequest);
				System.out.println(sol.getId() +" "+ geocoderResponse);
				
				if(geocoderResponse.getStatus() != null && geocoderResponse.getStatus().equals(GeocoderStatus.OK)) {
					lat = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat().doubleValue();
					lon = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng().doubleValue();
					
					sol.setNorteY((float) lat);
					sol.setEsteX((float) lon);
					
					service.saveSolicitud(sol);					
				}
				
				coord.setLat(lat);
				coord.setLon(lon);
				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return coord; 
	}
	

    public void cargarTasaciones(SolicitudTasacion sol){		
		//obtiene las solicitud pasadas en base a la comuna y región
		List<SolicitudTasacion> tasaciones = service.getTasacionesByRegionAndComuna(sol.getBien().getComuna());
    	//List<SolicitudTasacion> tasaciones = service.getTasacionesByCoordenadas(sol.getBien().getComuna().getId(),sol.getNorteY(), sol.getEsteX());
	
		//agrega las tasaciones realizadas
		for(SolicitudTasacion tasacion : tasaciones) {
			//lo agrega solo si tiene sentido
			if(tasacion.getCliente() != null && tasacion.getNorteY() != 0  && tasacion.getEsteX() != 0 ) {
				String ruta_img = null;
				switch (tasacion.getEstado()) {
				case CREADA:
					ruta_img = "VAADIN/img/pin_tas_asignada.png";
					break;
				case TASADA:
					ruta_img = "VAADIN/img/pin_tas_visitada.png";
					break;
				case VISADA:
					ruta_img = "VAADIN/img/pin_tas_visada.png";
					break;
				default:
					break;			
				}
				
//				GoogleMapMarker marker = googleMap.addMarker("Tasación "+tasacion.getNumeroTasacion()+" "+tasacion.getEstado().toString()+": "+tasacion.getCliente().getNombreCliente()+"\n"+
//									"Tasador: "+((tasacion.getTasador() != null)?tasacion.getTasador().getFullname():"No requiere")+"\n"+
//									"Tipo Bien: "+tasacion.getBien().getClase().toString()+", "+tasacion.getBien().getTipo().toString()+"\n"+
//									"Fecha Encargo: "+Utils.formatoFecha(tasacion.getFechaEncargo()), new LatLon(
//									tasacion.getNorteY(),tasacion.getEsteX()), false, ruta_img);				
				
				//no consideramos la tasación que ha sido seleccionada
				if(!sol.getNumeroTasacion().equals(tasacion.getNumeroTasacion()))
					googleMap.addMarker(tasacion.getNumeroTasacion(), new LatLon(tasacion.getNorteY(),tasacion.getEsteX()),false,ruta_img);
			}
		}
		
		googleMap.setMinZoom(4);
		googleMap.setMaxZoom(16);
	}
}
