package cl.koritsu.valued.view.transactions;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import ru.xpoft.vaadin.VaadinView;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.services.MailService;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.transactions.EditorSolicitudTasacion.OnClickRegresarListener;
import cl.koritsu.valued.view.transactions.EditorSolicitudTasacion.OnClickSiguienteListener;
import cl.koritsu.valued.view.transactions.MapToolBox.OnClickTasacionEvent;
import cl.koritsu.valued.view.utils.Constants;
import cl.koritsu.valued.view.utils.OpenInfoWindowOnMarkerClickListener;
import cl.koritsu.valued.view.utils.ResumenTasacion;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderComponent;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderStatus;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = MisSolicitudesView.NAME, cached = true)
public class MisSolicitudesView extends VerticalLayout implements View {
	
	Logger logger = LoggerFactory.getLogger(MisSolicitudesView.class);
	
	public static final String NAME = "en_proceso";

	FormLayout details, detailsIngreso;
    Label consoleEntry;
    GoogleMap googleMap;
    private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
    
    private GoogleMapInfoWindow infoWindow;
    
    OpenInfoWindowOnMarkerClickListener infoWindowOpener;
    @Autowired
    ValuedService service;
	@Autowired
	private transient MailService mailService;
    
    MapToolBox mapToolBox = new MapToolBox();

    public MisSolicitudesView() {
    	
    }
    
    @PostConstruct
    public void init() {
    	addStyleName("transactions");
    	ValuedEventBus.register(this);

    	addComponent(buildToolbar());
    	
        googleMap = new GoogleMap(apiKey, null, null);
        googleMap.setZoom(10);
        googleMap.setSizeFull();
        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);

        Panel mapsPanel = new Panel();
        mapsPanel.setSizeFull();
        mapsPanel.setHeight("800px");
        mapsPanel.setContent(googleMap);
        addComponent(mapsPanel);   
  		
        //permite identificar el punto que se esta clickeando para levantar la ventana de resumen.
        googleMap.addMarkerClickListener(new MarkerClickListener() {
		
			@Override
			public void markerClicked(GoogleMapMarker clickedMarker) {				
				infoWindow = new GoogleMapInfoWindow("Solicitud Tasación", clickedMarker);
				
				SolicitudTasacion sol = service.getSolicitudByNumeroTasacion(clickedMarker.getCaption());
			    infoWindow.setPosition(clickedMarker.getPosition());
				
				googleMap.setInfoWindowContents(infoWindow, new ResumenTasacion(sol, service, false,false));
				googleMap.openInfoWindow(infoWindow);				
			}
        });
       
      
        
        //situamos, inicialmente, el mapa en Santiago.
      	googleMap.setCenter(new LatLon(-33.448779, -70.668551));
        
        mapsPanel.setContent(googleMap);
    	
    	//agrega el listener del mapToolBox para cuando se presiona una tasacion
    	mapToolBox.addOnClickTasacionEvent(new OnClickTasacionEvent() {
			
			@Override
			public void onClick(BeanItem<SolicitudTasacion> solicitudBean) {

				SolicitudTasacion sol = solicitudBean.getBean();
				double lat = sol.getNorteY();
				double lon = sol.getEsteX();
				
				//cuando las coordenadas son igual a 0 tenemos que ir a buscarlas para almacenarla en la solicitud.
				if(lat == 0 || lon == 0){
					LatLon coordenadas = recuperarCoordenadas(sol);
					if(coordenadas.getLat() > 0 && coordenadas.getLon() > 0){
						googleMap.setCenter(new LatLon(coordenadas.getLat(),coordenadas.getLon()));
						googleMap.addMarker(sol.getNumeroTasacion(), new LatLon(coordenadas.getLat(), coordenadas.getLon()), true, "VAADIN/img/pin_tas_asignada.png");
					}
				}else{
					googleMap.setCenter(new LatLon(lat,lon));
					googleMap.addMarker(sol.getNumeroTasacion(), new LatLon(lat, lon), true, "VAADIN/img/pin_tas_asignada.png");
				}
				
				googleMap.setZoom(20);
				
				if(sol.getBien() != null && sol.getBien().getComuna() != null)
					cargarTasaciones(sol);
			
			}
		});
    	
    	mapToolBox.sendEmailOnClickEvent(new OnClickTasacionEvent() {
			
			@Override
			public void onClick(BeanItem<SolicitudTasacion> solicitudBean) {

				SolicitudTasacion sol = solicitudBean.getBean();
				if(sol != null)
					mailService.enviarAlertaVisitaVencida(sol);
				
			}
		});
    	
    	mapToolBox.addOnClickRegresarEvent(new OnClickRegresarListener() {
			
			@Override
			public void onClick(BeanItem<SolicitudTasacion> sol) {
		    	googleMap.clearMarkers();
		    	googleMap.setCenter(new LatLon(-33.448779, -70.668551));
				
			}
		});
    	
    	mapToolBox.addOnClickSiguienteEvent(new OnClickSiguienteListener() {
			
			@Override
			public void onClick(BeanItem<SolicitudTasacion> sol) {
				//guarda el elemento
				service.saveSolicitud(sol.getBean());
			}
		});
    	
    	UI.getCurrent().addWindow(mapToolBox);
    }

  
    
    private LatLon recuperarCoordenadas(SolicitudTasacion sol) {
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

	@Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        ValuedEventBus.unregister(this);
    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Mis Tasaciones en Curso");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);
        
        return header;
    }    
    
    @Override
    public void enter(final ViewChangeEvent event) {
    	//llena con las tasaciones
    	//si el usuario es admin@admin.com llena con la comuna por defecto
    	 Usuario user  = (Usuario) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO);
    	 List<SolicitudTasacion> solicitudes = null;
    	if(user.getEmail().equals("admin@admin.com")){
	    	solicitudes = service.getTasacionesEnProceso();
    	}else { // si no, toma las tasaciones asociadas al usuario 
    		solicitudes = service.getTasacionesEnProcesoByTasador(user);
    	}
    	mapToolBox.setSolicitudes(solicitudes);
    	
    }
    
    /*
     * Permite cargar en el mapa las tasaciones historicas.
     */
    private void cargarTasaciones(SolicitudTasacion sol){		
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
