package cl.koritsu.valued.view.nuevatasacion;

import java.io.IOException;
import java.util.List;

import org.vaadin.teemu.wizards.WizardStep;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderComponent;
import com.google.code.geocoder.model.GeocoderRequest;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.OutOfBoundsException;
import com.vaadin.ui.GridLayout.OverlapsException;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.enums.ClaseBien;
import cl.koritsu.valued.domain.enums.EstadoTasacion;
import cl.koritsu.valued.domain.enums.TipoBien;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.utils.Utils;

public class BienStep implements WizardStep {
	
	//@Value("${google.maps.api.key}")
	private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
	private ComboBox cbRegion,cbComuna;
	private TextField tfCalle, tfNumero;
	GoogleMap googleMap;
	BeanFieldGroup<SolicitudTasacion> fg;
	ValuedService service;
	GridLayout glRoot = new GridLayout(4,20);
	
	public BienStep(BeanFieldGroup<SolicitudTasacion> fg, ValuedService service) {
		this.fg =  fg;
		this.service = service;
		
		initView();
	}

	private void initView() {
		glRoot.setSpacing(true);
		glRoot.setMargin(true);
		//gl.setSizeFull();
		glRoot.setWidth("100%");
		
		// clase de bien
		glRoot.addComponents(new Label("Clase Bien"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				OptionGroup option = new OptionGroup();
				Utils.bind(fg,option,"bien.clase");
				int i = 0;
				for(ClaseBien cb : ClaseBien.values()) {
					option.addItem(cb);
					if(i == 0)
						option.setValue(cb);
					i++;
				}
				addComponents(option);
			}
		});
		
		try {
			glRoot.addComponent(mapaInicial(),2,0,3,6);				
		} catch (OverlapsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OutOfBoundsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//tipo de bien
		glRoot.addComponents(new Label("Tipo de Bien"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				Utils.bind(fg,tf, "bien.tipo");
				int i = 0;
				for(TipoBien tipo : TipoBien.values()) {
					tf.addItem(tipo);
					if(i == 0)
						tf.setValue(tipo);
					i++;
				}
				addComponents(tf);
			}
		});
		
		//region
		glRoot.addComponents(new Label("Región"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				cbRegion = new ComboBox();
				cbRegion.setContainerDataSource(new BeanItemContainer<Region>(Region.class));
				cbRegion.setItemCaptionMode(ItemCaptionMode.PROPERTY);
				cbRegion.setItemCaptionPropertyId("nombre");
				
				//obtiene la lista de regiones
				List<Region> regiones = service.getRegiones();
				((BeanItemContainer<Region>)cbRegion.getContainerDataSource()).addAll(regiones);

				addComponents(cbRegion);
				
				//deja seleccionada la region metropolitana
				cbRegion.select(new Region() {{setId(15l);}});
			}
		});
		
		//comuna
		glRoot.addComponents(new Label("Comuna"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				cbComuna = new ComboBox();
				Utils.bind(fg,cbComuna,"bien.comuna");
				cbComuna.setContainerDataSource(new BeanItemContainer<Comuna>(Comuna.class));
				cbComuna.setItemCaptionMode(ItemCaptionMode.PROPERTY);
				cbComuna.setItemCaptionPropertyId("nombre");
				addComponents(cbComuna);
			}
		});
		
		cbRegion.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				Region region = (Region) event.getProperty().getValue();
				cbComuna.setEnabled(true);
				//obtiene la lista de regiones
				List<Comuna> comunas = service.getComunaPorRegion(region);
				cbComuna.removeAllItems();
				((BeanItemContainer<Comuna>)cbComuna.getContainerDataSource()).addAll(comunas);

			}
		});
		
		//calle
		glRoot.addComponents(new Label("Calle"));
		glRoot.addComponent(new HorizontalLayout(){
			{				
				setSpacing(true);
				tfCalle = new TextField();
				Utils.bind(fg,tfCalle,"bien.direccion");
				
				tfCalle.addValueChangeListener(new ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						 try {
							String calle = (tfCalle.getValue().toString() != null)?tfCalle.getValue().toString()+" ":"";
							if(cbComuna.getValue()!= null && cbRegion.getValue() != null )
								refreshMap(calle.concat(((Comuna)cbComuna.getValue()).getNombre().toString()+" ").concat(((Region)cbRegion.getValue()).getNombre().toString()), 12);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

				addComponents(tfCalle);
			}
		});
		
		//solicitante
		glRoot.addComponents(new Label("Número"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				tfNumero = new TextField();
				Utils.bind(fg,tfNumero,"bien.numeroManzana");
				
				tfNumero.addValueChangeListener(new ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						 try {
							 String nm = (tfNumero.getValue().toString() != null)?tfNumero.getValue().toString()+" ":"";
							 String calle = (tfCalle.getValue().toString() != null)?tfCalle.getValue().toString()+" ":"";
							 if(cbComuna.getValue()!= null && cbRegion.getValue() != null )
								 refreshMap(calle.concat(nm).concat(((Comuna)cbComuna.getValue()).getNombre().toString()+" ").concat(((Region)cbRegion.getValue()).getNombre().toString()), 20);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				addComponents(tfNumero);
			}
		});
		
		//solicitante
		glRoot.addComponents(new Label("Unidad"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"bien.numeroPredial");
				addComponents(tf);
			}
		});
		
		// propietario
		glRoot.addComponent(new Label("Información sobre propietario") {{setStyleName("h2");}},0,7,3,7);
		
		//rut
		glRoot.addComponents(new Label("RUT"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				tf.setInputPrompt("11111111-1");
				Utils.bind(fg,tf,"rutPropietario");
				addComponents(tf);
			}
		});
		
		//telefono
		glRoot.addComponents(new Label("Teléfono"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"telefonoPropietario");
				addComponents(tf);
			}
		});
		
		//nombre
		glRoot.addComponents(new Label("Nombre"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"nombrePropietario");
				addComponents(tf);
			}
		});
		
		//correo
		glRoot.addComponents(new Label("Correo"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"emailPropietario");
				addComponents(tf);
			}
		});
		
		// contacto
		glRoot.addComponent(new Label("Información sobre contacto") {{setStyleName("h2");}},0,10,3,10);
		
		//nombre
		glRoot.addComponents(new Label("Nombre"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"nombreContacto");
				addComponents(tf);
			}
		});
		
		//telefono
		glRoot.addComponents(new Label("Teléfono Fijo"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"telefonoFijoContacto");
				addComponents(tf);
			}
		});
		
		//rut
		glRoot.addComponents(new Label("Teléfono Movil"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"telefonoMovilContacto");
				addComponents(tf);
			}
		});
		
		//correo
		glRoot.addComponents(new Label("Correo"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"emailContacto");
				addComponents(tf);
			}
		});
		
		// contacto
		glRoot.addComponent(new Label("Información sobre contacto 2") {{setStyleName("h2");}},0,13,3,13);
		
		//nombre
		glRoot.addComponents(new Label("Nombre"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"nombreContacto2");
				addComponents(tf);
			}
		});
		
		//telefono
		glRoot.addComponents(new Label("Teléfono Fijo"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"telefonoFijoContacto2");
				addComponents(tf);
			}
		});
		
		//rut
		glRoot.addComponents(new Label("Teléfono Movil"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"telefonoMovilContacto2");
				addComponents(tf);
			}
		});
		
		//correo
		glRoot.addComponents(new Label("Correo"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf,"emailContacto2");
				addComponents(tf);
			}
		});
	}

	@Override
	public String getCaption() {
		return "Bien";
	}

	@Override
	public Component getContent() {
		return glRoot;
	}

	@Override
	public boolean onAdvance() {
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}

	/*
	 * Permite crear el mapa inicial, sin un marker definido.
	 */	
	private VerticalLayout mapaInicial(){		
		VerticalLayout vl = new VerticalLayout();
		vl.setCaption("Mapa");
		vl.setSizeFull();
        		
		googleMap = new GoogleMap(apiKey, null, "english");
		//situamos, inicialmente, el mapa en Santiago.
		googleMap.setCenter(new LatLon(-33.448779, -70.668551));
		googleMap.setSizeFull();	
		
		//rescatamos las tasaciones realizadas desde la base...
//		googleMap.addMarker("Tasación Visada: Inmobiliaria1 S.A.", new LatLon(
//				-33.484747, -70.739321), false, "VAADIN/img/pin_tas_ing.png");	
//		googleMap.addMarker("Tasación Visada: Inmobiliaria2 S.A.", new LatLon(
//				-33.429363, -70.615143), false, "VAADIN/img/pin_tas_ing.png");	
//		googleMap.addMarker("Tasación Visada: Inmobiliaria3 S.A.", new LatLon(
//				-33.397766, -70.597899), false, "VAADIN/img/pin_tas_ing.png");	
//		googleMap.addMarker("Tasación Visada: Inmobiliaria4 S.A.", new LatLon(
//				-33.610913, -70.879582), false, "VAADIN/img/pin_tas_ing.png");
		//obtiene las solicitud pasadas
		List<SolicitudTasacion> tasaciones = service.getTasaciones();

		//agrega las tasaciones realizadas
		for(SolicitudTasacion tasacion : tasaciones) {
			//lo agrega solo si tiene sentido
			if(tasacion.getCliente() != null && tasacion.getNorteY() != 0  && tasacion.getEsteX() != 0 ) {
				googleMap.addMarker("Tasación "+tasacion.getEstado()+": "+tasacion.getCliente().getNombreCliente(), new LatLon(
						tasacion.getNorteY(),tasacion.getEsteX()), false, "VAADIN/img/pin_tas_ing.png");
			}
		}
		
		googleMap.setMinZoom(4);
		googleMap.setMaxZoom(16);
		
		/*
		 * Permite añadir, en la parte inferior del mapa, la historia de las coordenadas por la que
		 * se arrastraron los puntos (draggable)
		 */
		/*googleMap.addMarkerDragListener(new MarkerDragListener() {
			@Override
			public void markerDragged(GoogleMapMarker draggedMarker,
                LatLon oldPosition) {
                Label consoleEntry = new Label("Marcador arrastrado desde ("
                    + oldPosition.getLat() + ", " + oldPosition.getLon()
                    + ") hacia (" + draggedMarker.getPosition().getLat()
                    + ", " + draggedMarker.getPosition().getLon() + ")");
                consoleLayout.addComponent(consoleEntry);
            }
        });*/
		
		vl.addComponent(googleMap);
		
		return vl;
	}
	
	/*
	 * Permite situar el mapa de acuerdo a los parametros ingresados por el usuario.
	 */
	private void refreshMap(String direccion, int zoom) throws IOException {
		Geocoder geo = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(direccion)
				.getGeocoderRequest();
		geocoderRequest.addComponent(GeocoderComponent.COUNTRY, "cl");
		GeocodeResponse geocoderResponse = geo.geocode(geocoderRequest);
		if(geocoderResponse.getStatus() != null) {
		    switch (geocoderResponse.getStatus()) {
		         case ERROR:
		        	 Notification.show("Se ha producido un error al realizar la búsqueda",Type.ERROR_MESSAGE);
		             break;
		         case INVALID_REQUEST:
		        	 Notification.show("La solicitud es invalida, revise los parametros ingresados.", Type.WARNING_MESSAGE);
		             break;
		         case OVER_QUERY_LIMIT:
		        	 Notification.show("Ha excedido la cuota permitida en su plan.", Type.ASSISTIVE_NOTIFICATION);
		             break;
		         case UNKNOWN_ERROR:
		        	 Notification.show("No se pudo procesar la solicitud por un error en el servidor.", Type.ERROR_MESSAGE);
		             break;
		         case REQUEST_DENIED:
			         Notification.show("Se ha rechazado la geolocalización.", Type.ASSISTIVE_NOTIFICATION);
		             break;
		         case ZERO_RESULTS:
		        	 Notification.show("No existen resultados para la dirección ingresada.", Type.TRAY_NOTIFICATION);
		             break;
				case OK:
					for(GoogleMapMarker item : googleMap.getMarkers()){
						if(item.getCaption().equals(EstadoTasacion.NUEVA_TASACION.toString())){
							googleMap.removeMarker(item);
						}
					}
					
					System.out.println("Ver Respuesta :"+geocoderResponse);					
					double lat = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat().doubleValue();
					double lon = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng().doubleValue();
					
					//setea las coordenadas en el objeto
					fg.getItemDataSource().getBean().setNorteY((float) lat);
					fg.getItemDataSource().getBean().setEsteX((float) lon);
					
					googleMap.setCenter(new LatLon(lat,lon));
					googleMap.addMarker(EstadoTasacion.NUEVA_TASACION.toString(), new LatLon(
							lat, lon), true, "VAADIN/img/pin_tas_process.png");
					googleMap.setZoom(zoom);					
				default:
					break;
		     }
		 }
	}
}
