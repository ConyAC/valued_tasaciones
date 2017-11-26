package cl.koritsu.valued.view.sales;

import java.io.IOException;

import org.vaadin.teemu.wizards.WizardStep;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerDragListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.OutOfBoundsException;
import com.vaadin.ui.GridLayout.OverlapsException;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cl.koritsu.valued.domain.enums.TIPO_BIEN;

public class BienStep implements WizardStep {
	
	//@Value("${google.maps.api.key}")
	private String apiKey = "AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
	private ComboBox cbRegion,cbComuna;
	private TextField tfCalle, tfNumero;
	GoogleMap googleMap;
	  
	@Override
	public String getCaption() {
		return "Bien";
	}

	@Override
	public Component getContent() {
		final GridLayout gl = new GridLayout(4,20);
		gl.setSpacing(true);
		gl.setMargin(true);
		//gl.setSizeFull();
		gl.setWidth("100%");
		
		// clase de bien
		gl.addComponents(new Label("Clase Bien"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				OptionGroup tf = new OptionGroup();
				tf.addItem("Inmueble");
				tf.addItem("Mueble");
				tf.setValue("Inmueble");
				addComponents(tf);
			}
		});
		
		try {
			gl.addComponent(mapaInicial(),2,0,3,6);				
		} catch (OverlapsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OutOfBoundsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//tipo de bien
		gl.addComponents(new Label("Tipo de Bien"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				int i = 0;
				for(TIPO_BIEN tipo : TIPO_BIEN.values()) {
					tf.addItem(tipo);
					if(i == 0)
						tf.setValue(tipo);
					i++;
				}
				addComponents(tf);
			}
		});
		
		//region
		gl.addComponents(new Label("Región"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				cbRegion = new ComboBox();
				int i = 0;
				for(String tipo : new String[] {
						"Arica y Parinacota",
						"Tarapacá",
						"Antofagasta",
						"Atacama",
						"Coquimbo",
						"Valparaíso",
						"Santiago",
						"Del Libertador Gral. Bernardo O'Higgins",
						"Del Maule",
						"Del Biobío",
						"De La Araucanía",
						"De Los Ríos",
						"De Los Lagos",
						"Aysén Del Gral. Carlos Ibañez Del Campo",
						"Magallanes Y De La Antártica Chilena"}) {
					cbRegion.addItem(tipo);
					if(i == 0)
						cbRegion.setValue(tipo);
					i++;
				}
				addComponents(cbRegion);
			}
		});
		
		//comuna
		gl.addComponents(new Label("Comuna"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				cbComuna = new ComboBox();
				int i = 0;
				for(String tipo : new String[] {"Arica",
						"Camarones",
						"Putre",
						"General Lagos",
						"Iquique",
						"Alto Hospicio",
						"Pozo Almonte",
						"Camiña",
						"Colchane",
						"Huara",
						"Pica",
						"Antofagasta",
						"Mejillones",
						"Sierra Gorda",
						"Taltal",
						"Calama",
						"Ollagüe",
						"San Pedro de Atacama",
						"Tocopilla",
						"María Elena",
						"Copiapó",
						"Caldera",
						"Tierra Amarilla",
						"Chañaral",
						"Diego de Almagro",
						"Vallenar",
						"Alto del Carmen",
						"Freirina",
						"Huasco",
						"La Serena",
						"Coquimbo",
						"Andacollo",
						"Maipú"}) {
					cbComuna.addItem(tipo);
					if(i == 0)
						cbComuna.setValue(tipo);
					i++;
				}
				addComponents(cbComuna);
			}
		});
		
		//calle
		gl.addComponents(new Label("Calle"));
		gl.addComponent(new HorizontalLayout(){
			{
//				final LocationTextField<GeocodedLocation> ltf = LocationTextField.<GeocodedLocation>newBuilder()
//			    .withCaption("Address:")
//			    .withDelayMillis(1200)
//			    .withType(GeocodedLocation.class)
//			    .withInitialValue(null)
//			    .withLocationProvider(OpenStreetMapGeocoder.getInstance())
//			    .withMinimumQueryCharacters(5)
//			    .withWidth("100%")
//			    .withHeight("40px")
//			    .withImmediate(true)
//			    .build();
				
				setSpacing(true);
				tfCalle = new TextField();
				
				tfCalle.addValueChangeListener(new ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						 try {
							String calle = (tfCalle.getValue().toString() != null)?tfCalle.getValue().toString()+" ":"";
							refreshMap(calle.concat(cbComuna.getValue().toString()+" ").concat(cbRegion.getValue().toString()), 12);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				addComponents(tfCalle);
			}
		});
		
		//solicitante
		gl.addComponents(new Label("Número"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				tfNumero = new TextField();
				
				tfNumero.addValueChangeListener(new ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						 try {
							 String nm = (tfNumero.getValue().toString() != null)?tfNumero.getValue().toString()+" ":"";
							 String calle = (tfCalle.getValue().toString() != null)?tfCalle.getValue().toString()+" ":"";
							 refreshMap(calle.concat(nm).concat(cbComuna.getValue().toString()+" ").concat(cbRegion.getValue().toString()), 20);
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
		gl.addComponents(new Label("Unidad"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		// propietario
		gl.addComponent(new Label("Información sobre propietario"),0,7,3,7);
		
		//rut
		gl.addComponents(new Label("RUT"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//telefono
		gl.addComponents(new Label("Teléfono"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//nombre
		gl.addComponents(new Label("Nombre"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//correo
		gl.addComponents(new Label("Correo"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		// contacto
		gl.addComponent(new Label("Información sobre contacto"),0,10,3,10);
		
		//rut
		gl.addComponents(new Label("RUT"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//telefono
		gl.addComponents(new Label("Teléfono"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//nombre
		gl.addComponents(new Label("Nombre"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//correo
		gl.addComponents(new Label("Correo"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		return gl;
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
		
		final Panel console = new Panel();
        console.setHeight("100px");
        final CssLayout consoleLayout = new CssLayout();
        console.setContent(consoleLayout);
        		
		googleMap = new GoogleMap(apiKey, null, "english");
		//situamos, inicialmente, el mapa en Santiago.
		googleMap.setCenter(new LatLon(-33.448779, -70.668551));
		googleMap.setSizeFull();	
		
		//rescatamos las tasaciones realizadas desde la base...
		googleMap.addMarker("Tasación Visada: Inmobiliaria1 S.A.", new LatLon(
				-33.484747, -70.739321), false, "VAADIN/img/pin_tas_ing.png");	
		googleMap.addMarker("Tasación Visada: Inmobiliaria2 S.A.", new LatLon(
				-33.429363, -70.615143), false, "VAADIN/img/pin_tas_ing.png");	
		googleMap.addMarker("Tasación Visada: Inmobiliaria3 S.A.", new LatLon(
				-33.397766, -70.597899), false, "VAADIN/img/pin_tas_ing.png");	
		googleMap.addMarker("Tasación Visada: Inmobiliaria4 S.A.", new LatLon(
				-33.610913, -70.879582), false, "VAADIN/img/pin_tas_ing.png");	
		
		googleMap.setMinZoom(4);
		googleMap.setMaxZoom(16);
		
		/*
		 * Permite añadir, en la parte inferior del mapa, la historia de las coordenadas por la que
		 * se arrastraron los puntos (draggable)
		 */
		googleMap.addMarkerDragListener(new MarkerDragListener() {
			@Override
			public void markerDragged(GoogleMapMarker draggedMarker,
                LatLon oldPosition) {
                Label consoleEntry = new Label("Marcador arrastrado desde ("
                    + oldPosition.getLat() + ", " + oldPosition.getLon()
                    + ") hacia (" + draggedMarker.getPosition().getLat()
                    + ", " + draggedMarker.getPosition().getLon() + ")");
                consoleLayout.addComponent(consoleEntry);
            }
        });
		
		vl.addComponent(googleMap);
		vl.addComponent(console);
		
		return vl;
	}
	
	/*
	 * Permite situar el mapa de acuerdo a los parametros ingresados por el usuario.
	 */
	private void refreshMap(String direccion, int zoom) throws IOException {
		System.out.println("Ver direccion :"+direccion);
		Geocoder geo = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(direccion).setLanguage("en")
				.getGeocoderRequest();
		GeocodeResponse geocoderResponse = geo.geocode(geocoderRequest);
		
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
				System.out.println("Ver Respuesta :"+geocoderResponse);
				if(!geocoderResponse.getResults().isEmpty()) {
					double lat = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat().doubleValue();
					double lon = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng().doubleValue();
					
					googleMap.setCenter(new LatLon(lat,lon));
					googleMap.addMarker("Solicitud de Tasación: BCI", new LatLon(
							lat, lon), true, "VAADIN/img/pin_tas_process.png");
					googleMap.setZoom(zoom);
				}
			default:
				break;
	     }		
	}
}
