package cl.koritsu.valued.view.transactions;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.maddon.FilterableListContainer;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.GoogleMapControl;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.InfoWindowClosedListener;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.events.MapMoveListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerDragListener;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapKmlLayer;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;


import cl.koritsu.valued.ValuedUI;
import cl.koritsu.valued.component.MovieDetailsWindow;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Transaction;
import cl.koritsu.valued.event.ValuedEvent.BrowserResizeEvent;
import cl.koritsu.valued.event.ValuedEvent.TransactionReportEvent;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.ValuedViewType;
import ru.xpoft.vaadin.VaadinView;

@SuppressWarnings({ "serial", "unchecked" })
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = TransactionsView2.NAME, cached = true)
public final class TransactionsView2 extends VerticalLayout implements View {
	
	public static final String NAME = "en_proceso";

    private Table table;
    GoogleMap googleMap;
    private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
    private static final DateFormat DATEFORMAT = new SimpleDateFormat(
            "MM/dd/yyyy hh:mm:ss a");
    private static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
    private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
            "theater", "room", "title", "seats" };
    
    VerticalLayout layout = new VerticalLayout();
    
    private GoogleMapMarker kakolaMarker = new GoogleMapMarker(
        "DRAGGABLE: Kakolan vankila", new LatLon(60.44291, 22.242415),
        true, null);
    private GoogleMapInfoWindow kakolaInfoWindow = new GoogleMapInfoWindow(
        "Kakola used to be a provincial prison.", kakolaMarker);
    private GoogleMapMarker maariaMarker = new GoogleMapMarker("Maaria",
        new LatLon(60.536403, 22.344648), false);
    private GoogleMapInfoWindow maariaWindow = new GoogleMapInfoWindow(
        "Maaria is a district of Turku", maariaMarker);
    ;
    private Button componentToMaariaInfoWindowButton;
    
    
    @Autowired
    ValuedService service;

    public TransactionsView2() {
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
       
        Window mapToolBox = new Window("Map Tool Box");
        mapToolBox.setClosable(false);
        mapToolBox.setResizable(false);
        mapToolBox.setPosition(210, 220);
        mapToolBox.setWidth("350px");
        mapToolBox.setHeight("520px");
        mapToolBox.addStyleName("mywindowstyle");
        UI.getCurrent().addWindow(mapToolBox);

        HorizontalLayout latlonLayout = new HorizontalLayout();
        latlonLayout.setSpacing(true);
        
        TextField latitude = new TextField("Lat");
        latitude.setWidth("100px");
        latitude.setNullSettingAllowed(true);
        latitude.setNullRepresentation("0.0");
        
        TextField longitude = new TextField("Long");
        longitude.setWidth("100px");
        longitude.setNullSettingAllowed(true);
        longitude.setNullRepresentation("0.0");
        
        latlonLayout.addComponent(latitude);
        latlonLayout.addComponent(longitude);
        
        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.setSpacing(true);
        
        Label currentLat = new Label();
        currentLat.setCaption("Current Latitude");
        
        Label currentLon = new Label();
        currentLon.setCaption("Current Longitude");

        infoLayout.addComponent(currentLat);
        infoLayout.addComponent(currentLon);
        
        TextField markerName = new TextField("Marker Name");

        Label latErrMsg = new Label();
        latErrMsg.addStyleName("mylabelstyle");
        Label lonErrMsg = new Label();
        lonErrMsg.addStyleName("mylabelstyle");
        
//        Button.ClickListener addMarkerListener = new ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				String mName = markerName.getValue();
//				if(mName.isEmpty()){
//					mName = "Marker";
//				}
//				Double dLat = 0.0; 
//				Double dLon = 0.0; 
//				dLat = Double.valueOf(currentLat.getValue());
//				dLon = Double.valueOf(currentLon.getValue());
//
//				GoogleMapMarker customMarker = new GoogleMapMarker(mName, new LatLon(dLat, dLon),true, null);
//				googleMap.addMarker(customMarker);
//			}
//		};
        
        Button addMarker = new Button("Add Marker", FontAwesome.ANCHOR);
  //      addMarker.addClickListener(addMarkerListener);
        
//        Button.ClickListener moveView = new ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				Boolean val = true;
//				Double dLat = 0.0; 
//				Double dLon = 0.0; 
//
//				try {
//					dLat = Double.valueOf(latitude.getValue());
//				} catch (Exception e) {
//					val = false;
//					latErrMsg.setValue("Latitude is not a valid number");
//				}
//				try {
//					dLon = Double.valueOf(longitude.getValue());
//				} catch (Exception e) {
//					val = false;
//					lonErrMsg.setValue("Longitude is not a valid number");
//				}
//				
//				if(val){
//					latErrMsg.setValue("");
//					lonErrMsg.setValue("");
//					if((dLat<= -85.0) || (dLat >= 85.0)){
//						val = false;
//						latErrMsg.setValue("Latitude must be between -85.0 and 85.0");
//					}
//					if((dLon<= -180.0) || (dLon >= 180.0)){
//						val = false;
//						lonErrMsg.setValue("Longitude  must be between -180.0 and 180.0");
//					}
//				}
//				
//				if(val){
//					latErrMsg.setValue("");
//					lonErrMsg.setValue("");
//	                googleMap.setCenter(new LatLon(dLat, dLon));
//	                googleMap.setZoom(12);
//	                currentLat.setValue(latitude.getValue());
//	                currentLon.setValue(longitude.getValue());
//				}
//			}
//		};
        
        Button moveButton = new Button("Move", FontAwesome.BULLSEYE);
       // moveButton.addClickListener(moveView);

//        Button.ClickListener clearMarkerListener = new ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//                googleMap.clearMarkers();
//			}
//		};

		Button clearMarkersButton = new Button("Clear markers", FontAwesome.REMOVE);
       // clearMarkersButton.addClickListener(clearMarkerListener);

        Double newyorkLat = 40.7128;
        Double newyorkLon = -74.0059;
        googleMap.setCenter(new LatLon(40.7128, -74.0059));
		GoogleMapMarker newyorkMarker = new GoogleMapMarker("New York", new LatLon(newyorkLat, newyorkLon),true, null);
		googleMap.addMarker(newyorkMarker);
		latitude.setValue(newyorkLat.toString());
		longitude.setValue(newyorkLon.toString());
		currentLat.setValue(latitude.getValue());
		currentLon.setValue(longitude.getValue());

        VerticalLayout toolLayout = new VerticalLayout();
        toolLayout.setMargin(true);
        toolLayout.setSpacing(true);
        
        table = buildTable();
        toolLayout.addComponent(table);
        mapToolBox.setContent(toolLayout);
    }

  
    
    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        //ValuedEventBus.unregister(this);
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

        HorizontalLayout tools = new HorizontalLayout(buildFilter());
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(final TextChangeEvent event) {
                Filterable data = (Filterable) table.getContainerDataSource();
                data.removeAllContainerFilters();
                data.addContainerFilter(new Filter() {
                    @Override
                    public boolean passesFilter(final Object itemId,
                            final Item item) {

                        if (event.getText() == null
                                || event.getText().equals("")) {
                            return true;
                        }

                        return filterByProperty("country", item,
                                event.getText())
                                || filterByProperty("city", item,
                                        event.getText())
                                || filterByProperty("title", item,
                                        event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("country")
                                || propertyId.equals("city")
                                || propertyId.equals("title")) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        filter.setInputPrompt("Filter");
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        filter.addShortcutListener(new ShortcutListener("Clear",
                KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
                filter.setValue("");
                ((Filterable) table.getContainerDataSource())
                        .removeAllContainerFilters();
            }
        });
        return filter;
    }

    private Table buildTable() {
        final Table table = new Table() {
            @Override
            protected String formatPropertyValue(final Object rowId,
                    final Object colId, final Property<?> property) {
                String result = super.formatPropertyValue(rowId, colId,
                        property);
                if (colId.equals("fechaEncargo")) {
                    result = DATEFORMAT.format(((Date) property.getValue()));
                } else if (colId.equals("price")) {
                    if (property != null && property.getValue() != null) {
                        return "$" + DECIMALFORMAT.format(property.getValue());
                    } else {
                        return "";
                    }
                }
                return result;
            }
        };
        table.setSizeFull();
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setSelectable(true);


        table.setColumnReorderingAllowed(true);
        table.setContainerDataSource(new BeanItemContainer<SolicitudTasacion>(SolicitudTasacion.class));
        table.setSortContainerPropertyId("time");
        table.setSortAscending(false);

        table.setColumnAlignment("seats", Align.RIGHT);
        table.setColumnAlignment("price", Align.RIGHT);
        
        table.addGeneratedColumn("vacio", new ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
		
				// Find the application directory
				String basepath = VaadinService.getCurrent()
				                  .getBaseDirectory().getAbsolutePath();

				// Image as a file resource
				FileResource resource = new FileResource(new File(basepath +
				                        "/VAADIN/img/pin_tas_ing.png"));
				return new Image(null,resource);
			}
		});
        
        table.addGeneratedColumn("nombrecliente", new ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId)).getBean();
				return sol.getCliente() != null ? sol.getCliente().getNombreCliente() : "";
			}
		});

        table.setVisibleColumns("vacio","numeroTasacion","fechaEncargo", "nombrecliente");
        table.setColumnHeaders("","N° Tasación", "Fecha visita", "Cliente");
        
        table.addGeneratedColumn("Acciones", new ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				return new Button(FontAwesome.EDIT){{}};
			}
		});

        table.setFooterVisible(true);
        table.setColumnFooter("time", "Total");

        // Allow dragging items to the reports menu
        table.setDragMode(TableDragMode.MULTIROW);
        table.setMultiSelect(true);

        return table;
    }

    private boolean defaultColumnsVisible() {
        boolean result = true;
        for (String propertyId : DEFAULT_COLLAPSIBLE) {
            if (table.isColumnCollapsed(propertyId) == Page.getCurrent()
                    .getBrowserWindowWidth() < 800) {
                result = false;
            }
        }
        return result;
    }

    @Subscribe
    public void browserResized(final BrowserResizeEvent event) {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.
        if (defaultColumnsVisible()) {
            for (String propertyId : DEFAULT_COLLAPSIBLE) {
                table.setColumnCollapsed(propertyId, Page.getCurrent()
                        .getBrowserWindowWidth() < 800);
            }
        }
    }

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

    void createNewReportFromSelection() {
        UI.getCurrent().getNavigator()
                .navigateTo(ValuedViewType.REPORTS.getViewName());
        ValuedEventBus.post(new TransactionReportEvent(
                (Collection<Transaction>) table.getValue()));
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    	//limpia la tabla
    	table.removeAllItems();
    	//llena con las tasaciones
    	Comuna comuna = new Comuna();
    	comuna.setId(15101L);
    	Region reg = new Region();
    	reg.setId(1L);
    	comuna.setRegion(reg);
    	List<SolicitudTasacion> solicitudes = service.getTasacionesByRegionAndComuna(comuna);
    	((BeanItemContainer<SolicitudTasacion>)table.getContainerDataSource()).addAll(solicitudes);
    }
    
    public void probando(VaadinRequest request) {
        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setSizeFull();
        addComponent(rootLayout);
        

        googleMap = new GoogleMap(apiKey, null, null);
        googleMap.setZoom(10);
        googleMap.setSizeFull();
        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);

        Panel mapsPanel = new Panel();
        mapsPanel.setSizeFull();
        mapsPanel.setContent(googleMap);
        rootLayout.addComponent(mapsPanel);

       
        Window mapToolBox = new Window("Map Tool Box");
        mapToolBox.setClosable(false);
        mapToolBox.setResizable(false);
        mapToolBox.setPosition(10, 100);
        mapToolBox.setWidth("350px");
        mapToolBox.setHeight("520px");
        mapToolBox.addStyleName("mywindowstyle");
        UI.getCurrent().addWindow(mapToolBox);

        HorizontalLayout latlonLayout = new HorizontalLayout();
        latlonLayout.setSpacing(true);
        
        TextField latitude = new TextField("Lat");
        latitude.setWidth("100px");
        latitude.setNullSettingAllowed(true);
        latitude.setNullRepresentation("0.0");
        
        TextField longitude = new TextField("Long");
        longitude.setWidth("100px");
        longitude.setNullSettingAllowed(true);
        longitude.setNullRepresentation("0.0");
        
        latlonLayout.addComponent(latitude);
        latlonLayout.addComponent(longitude);
        
        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.setSpacing(true);
        
        Label currentLat = new Label();
        currentLat.setCaption("Current Latitude");
        
        Label currentLon = new Label();
        currentLon.setCaption("Current Longitude");

        infoLayout.addComponent(currentLat);
        infoLayout.addComponent(currentLon);
        
        TextField markerName = new TextField("Marker Name");

        Label latErrMsg = new Label();
        latErrMsg.addStyleName("mylabelstyle");
        Label lonErrMsg = new Label();
        lonErrMsg.addStyleName("mylabelstyle");
        
//        Button.ClickListener addMarkerListener = new ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				String mName = markerName.getValue();
//				if(mName.isEmpty()){
//					mName = "Marker";
//				}
//				Double dLat = 0.0; 
//				Double dLon = 0.0; 
//				dLat = Double.valueOf(currentLat.getValue());
//				dLon = Double.valueOf(currentLon.getValue());
//
//				GoogleMapMarker customMarker = new GoogleMapMarker(mName, new LatLon(dLat, dLon),true, null);
//				googleMap.addMarker(customMarker);
//			}
//		};
        
        Button addMarker = new Button("Add Marker", FontAwesome.ANCHOR);
  //      addMarker.addClickListener(addMarkerListener);
        
//        Button.ClickListener moveView = new ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				Boolean val = true;
//				Double dLat = 0.0; 
//				Double dLon = 0.0; 
//
//				try {
//					dLat = Double.valueOf(latitude.getValue());
//				} catch (Exception e) {
//					val = false;
//					latErrMsg.setValue("Latitude is not a valid number");
//				}
//				try {
//					dLon = Double.valueOf(longitude.getValue());
//				} catch (Exception e) {
//					val = false;
//					lonErrMsg.setValue("Longitude is not a valid number");
//				}
//				
//				if(val){
//					latErrMsg.setValue("");
//					lonErrMsg.setValue("");
//					if((dLat<= -85.0) || (dLat >= 85.0)){
//						val = false;
//						latErrMsg.setValue("Latitude must be between -85.0 and 85.0");
//					}
//					if((dLon<= -180.0) || (dLon >= 180.0)){
//						val = false;
//						lonErrMsg.setValue("Longitude  must be between -180.0 and 180.0");
//					}
//				}
//				
//				if(val){
//					latErrMsg.setValue("");
//					lonErrMsg.setValue("");
//	                googleMap.setCenter(new LatLon(dLat, dLon));
//	                googleMap.setZoom(12);
//	                currentLat.setValue(latitude.getValue());
//	                currentLon.setValue(longitude.getValue());
//				}
//			}
//		};
        
        Button moveButton = new Button("Move", FontAwesome.BULLSEYE);
       // moveButton.addClickListener(moveView);

//        Button.ClickListener clearMarkerListener = new ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//                googleMap.clearMarkers();
//			}
//		};

		Button clearMarkersButton = new Button("Clear markers", FontAwesome.REMOVE);
       // clearMarkersButton.addClickListener(clearMarkerListener);

        Double newyorkLat = 40.7128;
        Double newyorkLon = -74.0059;
        googleMap.setCenter(new LatLon(40.7128, -74.0059));
		GoogleMapMarker newyorkMarker = new GoogleMapMarker("New York", new LatLon(newyorkLat, newyorkLon),true, null);
		googleMap.addMarker(newyorkMarker);
		latitude.setValue(newyorkLat.toString());
		longitude.setValue(newyorkLon.toString());
		currentLat.setValue(latitude.getValue());
		currentLon.setValue(longitude.getValue());

        VerticalLayout toolLayout = new VerticalLayout();
        toolLayout.setMargin(true);
        toolLayout.setSpacing(true);
        mapToolBox.setContent(toolLayout);
        toolLayout.addComponent(clearMarkersButton);
        toolLayout.addComponent(latlonLayout);
        toolLayout.addComponent(moveButton);
        toolLayout.addComponent(infoLayout);
        toolLayout.addComponent(markerName);
        toolLayout.addComponent(addMarker);
        toolLayout.addComponent(latErrMsg);
        toolLayout.addComponent(lonErrMsg);
	}
}
