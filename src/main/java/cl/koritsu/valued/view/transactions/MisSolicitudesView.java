package cl.koritsu.valued.view.transactions;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import ru.xpoft.vaadin.VaadinView;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.domain.enums.EtapaTasacion;
import cl.koritsu.valued.domain.enums.Permiso;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.services.MailService;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.transactions.EditorSolicitudTasacion.OnClickRegresarListener;
import cl.koritsu.valued.view.transactions.EditorSolicitudTasacion.OnClickSiguienteListener;
import cl.koritsu.valued.view.transactions.MapToolBox.OnClickTasacionEvent;
import cl.koritsu.valued.view.utils.Constants;
import cl.koritsu.valued.view.utils.MapaTasacion;
import cl.koritsu.valued.view.utils.OpenInfoWindowOnMarkerClickListener;
import cl.koritsu.valued.view.utils.ResumenTasacion;
import cl.koritsu.valued.view.utils.SecurityHelper;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerDragListener;
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

        final MapaTasacion mapaTasacion = new MapaTasacion(service, googleMap);
        
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
					LatLon coordenadas = mapaTasacion.recuperarCoordenadas(sol);
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
					mapaTasacion.cargarTasaciones(sol);
			
				/**
				 * Mostramos las coordenadas en base al arrastre del marker
				 */
				googleMap.addMarkerDragListener(new MarkerDragListener() {
					@Override
					public void markerDragged(GoogleMapMarker draggedMarker,
		                LatLon oldPosition) {
		                mapToolBox.setCoordenadasTasacion("Marcador arrastrado desde ("
			                    + oldPosition.getLat() + ", " + oldPosition.getLon()
			                    + ") hacia (" + draggedMarker.getPosition().getLat()
			                    + ", " + draggedMarker.getPosition().getLon() + ")");
		            }
		        });
				
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
				guardarEnBitacora(sol.getBean());
			}
		});
    	
    	UI.getCurrent().addWindow(mapToolBox);
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
    		if( SecurityHelper.hasPermission(Permiso.VISAR_TASACION ) ) {
	    		//si tiene el permiso de tasador, entonces agrega las tasaciones que tengan el estado tasadas
	    		List<SolicitudTasacion> solicitudesTasadas = service.getTasacionesByEstado(EstadoSolicitud.TASADA);
	    		solicitudes.addAll(solicitudesTasadas);
    		}
    	}
    	mapToolBox.setSolicitudes(solicitudes);
    	
    }    
    
    public void guardarEnBitacora(SolicitudTasacion sol){
    	
    	switch (sol.getEstado()) {
		case AGENDADA:
			service.saveBitacora(sol, EtapaTasacion.AGENDAR_VISITA);
			break;
		case AGENDADA_CON_INCIDENCIA:
			service.saveBitacora(sol, EtapaTasacion.CREAR_INCIDENCIA);
			break;
		case TASADA:
			service.saveBitacora(sol, EtapaTasacion.INGRESAR_INFORMACION);
		case VISADA:
			service.saveBitacora(sol, EtapaTasacion.ENVIAR_A_CLIENTE);
			break;
		case VISADA_CLIENTE:
			service.saveBitacora(sol, EtapaTasacion.ENVIAR_A_CLIENTE);
			break;
		case VISITADA:
			service.saveBitacora(sol, EtapaTasacion.CONFIRMAR_VISITA);
			break;
		}   	
    }
}
