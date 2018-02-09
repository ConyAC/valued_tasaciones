package cl.koritsu.valued.view.transactions;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerDragListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.ObraComplementaria;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Transaction;
import cl.koritsu.valued.domain.enums.Adicional;
import cl.koritsu.valued.domain.enums.EstadoTasacion;
import cl.koritsu.valued.domain.enums.Programa;
import cl.koritsu.valued.event.ValuedEvent.BrowserResizeEvent;
import cl.koritsu.valued.event.ValuedEvent.TransactionReportEvent;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.ValuedViewType;
import cl.koritsu.valued.view.transactions.MapToolBox.OnClickTasacionEvent;
import cl.koritsu.valued.view.utils.Utils;
import ru.xpoft.vaadin.VaadinView;

@SuppressWarnings({ "serial", "unchecked" })
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = MisSolicitudesView.NAME, cached = true)
public final class MisSolicitudesView extends VerticalLayout implements View {
	
	public static final String NAME = "en_proceso";

	FormLayout details, detailsIngreso;
    Label consoleEntry;
    GoogleMap googleMap;
    private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";

    @Autowired
    ValuedService service;
    
    MapToolBox mapToolBox = new MapToolBox();

    public MisSolicitudesView() {
    	
    }
    
    @PostConstruct
    void init() {
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
				googleMap.setCenter(new LatLon(lat,lon));
				googleMap.addMarker(EstadoTasacion.NUEVA_TASACION.toString(), new LatLon(
						lat, lon), true, "VAADIN/img/pin_tas_asignada.png");
				googleMap.setZoom(20);
				
				if(sol.getBien() != null && sol.getBien().getComuna() != null)
					cargarTasaciones(sol.getBien().getComuna());
				
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

//        HorizontalLayout tools = new HorizontalLayout(buildFilter());
//        tools.setSpacing(true);
//        tools.addStyleName("toolbar");
//        header.addComponent(tools);
        
        return header;
    }
    
    /*
     * Cambiar filtro segun perfil
     */
//    private Component buildFilter() {
//        final TextField filter = new TextField();
//        filter.addTextChangeListener(new TextChangeListener() {
//            @Override
//            public void textChange(final TextChangeEvent event) {
//                Filterable data = (Filterable) table.getContainerDataSource();
//                data.removeAllContainerFilters();
//                data.addContainerFilter(new Filter() {
//                    @Override
//                    public boolean passesFilter(final Object itemId,
//                            final Item item) {
//
//                        if (event.getText() == null
//                                || event.getText().equals("")) {
//                            return true;
//                        }
//
//                        return filterByProperty("numeroTasacion", item,
//                                event.getText())
//                                || filterByProperty("estado", item,
//                                        event.getText())
//                                || filterByProperty("bien.direccion", item,
//                                        event.getText());
//
//                    }
//
//                    @Override
//                    public boolean appliesToProperty(final Object propertyId) {
//                        if (propertyId.equals("numeroTasacion")
//                                || propertyId.equals("estado")
//                                || propertyId.equals("bien.direccion")) {
//                            return true;
//                        }
//                        return false;
//                    }
//                });
//            }
//        });
//
//        filter.setInputPrompt("Filter");
//        filter.setIcon(FontAwesome.SEARCH);
//        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
//        filter.addShortcutListener(new ShortcutListener("Clear",
//                KeyCode.ESCAPE, null) {
//            @Override
//            public void handleAction(final Object sender, final Object target) {
//                filter.setValue("");
//                ((Filterable) table.getContainerDataSource())
//                        .removeAllContainerFilters();
//            }
//        });
//        return filter;
//    }
    
    private boolean filterByProperty(final String prop, final Item item,
            final String text) {
        if (item == null || item.getItemProperty(prop) == null
                || item.getItemProperty(prop).getValue() == null) {
            return false;
        }
        String val = item.getItemProperty(prop).getValue().toString().trim()
                .toLowerCase();
        if (val.contains(text.toLowerCase().trim())) {
            return true;
        }
        return false;
    }
    
    
    @Override
    public void enter(final ViewChangeEvent event) {
    	//llena con las tasaciones
    	Comuna comuna = new Comuna();
    	comuna.setId(13101L);
    	List<SolicitudTasacion> solicitudes = service.getTasacionesByRegionAndComuna(comuna);
    	mapToolBox.setSolicitudes(solicitudes);
    	
    }
    
    /*
     * Permite cargar en el mapa las tasaciones historicas.
     */
    private void cargarTasaciones(Comuna c){		
		//obtiene las solicitud pasadas en base a la comuna y región
		List<SolicitudTasacion> tasaciones = service.getTasacionesByRegionAndComuna(c);
	
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
				
				googleMap.addMarker("Tasación "+tasacion.getEstado().toString()+": "+tasacion.getCliente().getNombreCliente()+"\n"+
									"Tasador: "+((tasacion.getTasador() != null)?tasacion.getTasador().getFullname():"No requiere")+"\n"+
									"Tipo Bien: "+tasacion.getBien().getClase().toString()+", "+tasacion.getBien().getTipo().toString()+"\n"+
									"Fecha Encargo: "+Utils.formatoFecha(tasacion.getFechaEncargo()), new LatLon(
									tasacion.getNorteY(),tasacion.getEsteX()), false, ruta_img);
			}
		}
		
		googleMap.setMinZoom(4);
		googleMap.setMaxZoom(16);
	}
}
