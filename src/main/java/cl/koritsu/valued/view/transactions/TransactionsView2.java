package cl.koritsu.valued.view.transactions;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.dialogs.ConfirmDialog;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerDragListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.Cargo;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Contacto;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Transaction;
import cl.koritsu.valued.domain.enums.EstadoTasacion;
import cl.koritsu.valued.event.ValuedEvent.BrowserResizeEvent;
import cl.koritsu.valued.event.ValuedEvent.TransactionReportEvent;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.ValuedViewType;
import cl.koritsu.valued.view.utils.Utils;
import ru.xpoft.vaadin.VaadinView;

@SuppressWarnings({ "serial", "unchecked" })
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = TransactionsView2.NAME, cached = true)
public final class TransactionsView2 extends VerticalLayout implements View {
	
	public static final String NAME = "en_proceso";

    Table table;
    Label consoleEntry;
    GoogleMap googleMap;
    private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
    private static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
    private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
            "theater", "room", "title", "seats" };

    @Autowired
    ValuedService service;
    
    BeanItemContainer<SolicitudTasacion> solicitudContainer = new BeanItemContainer<SolicitudTasacion>(SolicitudTasacion.class);
 
    Window mapToolBox = new Window();

    public TransactionsView2() {
    	
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
       
        mapToolBox.setClosable(false);
        mapToolBox.setResizable(true);
        mapToolBox.setPosition(210, 220);
        //FIXME
        mapToolBox.setWidth("550px");
        mapToolBox.setHeight("520px");
        mapToolBox.addStyleName("mywindowstyle");
        
             
        //situamos, inicialmente, el mapa en Santiago.
      	googleMap.setCenter(new LatLon(-33.448779, -70.668551));
		
        table = buildTable();
        mapsPanel.setContent(googleMap);
        
        mapToolBox.setContent(table);
        mapToolBox.setData("no_cerrar");
    	UI.getCurrent().addWindow(mapToolBox);
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

        return header;
    }
    
    private Table buildTable() {
        final Table table = new Table() {
            @Override
            protected String formatPropertyValue(final Object rowId,
                    final Object colId, final Property<?> property) {
                String result = super.formatPropertyValue(rowId, colId,
                        property);
                if (colId.equals("fechaEncargo")) {
                    result = Utils.formatoFecha(((Date) property.getValue()));
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
       // table.setSizeFull();
//        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
//        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
//        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setSelectable(true);


        table.setColumnReorderingAllowed(true);
        table.setContainerDataSource(new BeanItemContainer<SolicitudTasacion>(SolicitudTasacion.class));
        table.setSortContainerPropertyId("time");
        table.setSortAscending(false);

        table.setColumnAlignment("seats", Align.RIGHT);
        table.setColumnAlignment("price", Align.RIGHT);
        
        table.addGeneratedColumn("nombrecliente", new ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId)).getBean();
				return sol.getCliente() != null ? sol.getCliente().getNombreCliente() : "";
			}
		});

        table.setVisibleColumns("numeroTasacion","fechaEncargo", "nombrecliente");
        table.setColumnHeaders("N° Tasación", "Fecha visita", "Cliente");
        
        table.addGeneratedColumn("Acciones", new ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				Button editarTasacion = new Button(null,FontAwesome.MAP_MARKER);
				editarTasacion.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId)).getBean();
						double lat = sol.getNorteY();
						double lon = sol.getEsteX();			
						googleMap.setCenter(new LatLon(lat,lon));
						googleMap.addMarker(EstadoTasacion.NUEVA_TASACION.toString(), new LatLon(
								lat, lon), true, "VAADIN/img/pin_tas_asignada.png");
						googleMap.setZoom(12);		
						
						mapToolBox.setContent(buildForm(sol));
					}
				});
				return editarTasacion;
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
    
    public VerticalLayout buildForm(SolicitudTasacion solicitud) {
    	final VerticalLayout vl = new VerticalLayout();
    	vl.setMargin(true);
    	
    	final FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        vl.addComponent(details);
        vl.setExpandRatio(details, 1);
      	
      	Label sectionCliente = new Label("Resumen");
      	sectionCliente.addStyleName(ValoTheme.LABEL_H3);
      	sectionCliente.addStyleName(ValoTheme.LABEL_COLORED);	    
	    details.addComponent(sectionCliente);
	    
	    Label encargo = new Label();
	    encargo.setCaption("Fecha Encargo");
	    encargo.setValue(Utils.formatoFecha(solicitud.getFechaEncargo()));
	    details.addComponent(encargo);
	    Label cliente = new Label();
	    cliente.setCaption("Nombre Cliente");
	    cliente.setValue(solicitud.getCliente().getRazonSocial().toString());
	    details.addComponent(cliente);
	    Label informe = new Label();
	    informe.setCaption("Tipo Informe");
	    informe.setValue(solicitud.getTipoInforme().getNombre().toString());
	    details.addComponent(informe);
	    Label clase = new Label();
	    clase.setCaption("Clase Bien");
	    clase.setValue(solicitud.getBien().getClase().toString());
	    details.addComponent(clase);
	    Label direccion = new Label();
	    direccion.setCaption("Dirección");
		direccion.setValue(solicitud.getBien().getDireccion() + " " + solicitud.getBien().getNumeroManzana() + ", "
				+ solicitud.getBien().getComuna().getNombre() + ", "
				+ solicitud.getBien().getComuna().getRegion().getNombre());
	    details.addComponent(direccion);
	    
        Label sectionBien = new Label("Ingreso");
	    sectionBien.addStyleName(ValoTheme.LABEL_H3);
	    sectionBien.addStyleName(ValoTheme.LABEL_COLORED);	    
	    details.addComponent(sectionBien);
	    
	    TextField superficie = new TextField();
	    superficie.setCaption("Superficie Total");
        details.addComponent(superficie);
	    TextField superficieBalcon = new TextField("Superficie Balcón/Terraza");
        details.addComponent(superficieBalcon);
	    TextField superficieTerraza = new TextField("Valor UF Balcón/Terraza");
        details.addComponent(superficieTerraza);
        
        Label obras = new Label("Obras Complementarias");
        obras.addStyleName(ValoTheme.LABEL_H3);
        obras.addStyleName(ValoTheme.LABEL_COLORED);	    
	    details.addComponent(obras);
	    
	    Button btnObras = new Button(null,FontAwesome.PLUS);
		details.addComponent(btnObras);
//		btnObras.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				;				
//			}
//		});	

	    
		details.addComponent(buildTableObras());	  
		
		Label programa = new Label("Programa");
		programa.addStyleName(ValoTheme.LABEL_H3);
		programa.addStyleName(ValoTheme.LABEL_COLORED);	    
	    details.addComponent(programa);
	    
	    Button btnPrograma = new Button(null,FontAwesome.PLUS);
		details.addComponent(btnPrograma);
//		btnPrograma.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				;				
//			}
//		});	
		
		details.addComponent(buildTablePrograma());
		
		Label coor = new Label("Coordenadas");
		coor.addStyleName(ValoTheme.LABEL_H3);
		coor.addStyleName(ValoTheme.LABEL_COLORED);	    
	    details.addComponent(coor);
	    
		/*
		 * Permite añadir, en la parte inferior del mapa, la historia de las coordenadas por la que
		 * se arrastraron los puntos (draggable)
		 */    
		googleMap.addMarkerDragListener(new MarkerDragListener() {
			@Override
			public void markerDragged(GoogleMapMarker draggedMarker,
                LatLon oldPosition) {
                consoleEntry = new Label("Marcador arrastrado desde ("
                    + oldPosition.getLat() + ", " + oldPosition.getLon()
                    + ") hacia (" + draggedMarker.getPosition().getLat()
                    + ", " + draggedMarker.getPosition().getLon() + ")");
                details.addComponent(consoleEntry);
            }
        });
		
		vl.addComponent(buildFooter());
		
	    return vl;
    }
    
    Button btnRegresar = new Button("Regresar");
    Button btnGuadar = new Button("Aceptar");
    
    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
//        footer.setWidth(100.0f, Unit.PERCENTAGE);

        btnGuadar.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnGuadar.setIcon(FontAwesome.SAVE);
        btnGuadar.focus();
		
        btnRegresar.addStyleName("link");        
        btnRegresar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				googleMap.clearMarkers();
				mapToolBox.setContent(table);
		      	googleMap.setCenter(new LatLon(-33.448779, -70.668551));
			}
		});	
        
        footer.addComponent(btnGuadar);
        footer.addComponent(btnRegresar);
        footer.setComponentAlignment(btnGuadar, Alignment.TOP_LEFT);
        footer.setComponentAlignment(btnRegresar, Alignment.TOP_LEFT);
        
        return footer;
    }
    
    private VerticalLayout buildTablePrograma() {
    	VerticalLayout vll = new VerticalLayout();
 
    	final Table tablePrograma = new Table();
		tablePrograma.setHeight("100px");
		tablePrograma.addContainerProperty("Elementos", ComboBox.class, null);
		tablePrograma.addContainerProperty("Cantidad/Superficies",  Integer.class, null);
		
		ComboBox cbProg = new ComboBox();
		cbProg.setWidth("150px");
		cbProg.addItem("Dormitoro");
		cbProg.addItem("Baño");
		
		tablePrograma.addItem(new Object[]{cbProg,2}, 1);

		tablePrograma.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el contacto seleccionado?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									;
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});
		
		vll.addComponent(tablePrograma);
		vll.setComponentAlignment(tablePrograma, Alignment.MIDDLE_LEFT);
		return vll;
    }
    
    private Table buildTableObras() {
		final Table tableObras = new Table();
		tableObras.setHeight("100px");
		tableObras.addContainerProperty("Elemento", ComboBox.class, null);
		tableObras.addContainerProperty("Cantidad/Superficie",  Integer.class, null);
		tableObras.addContainerProperty("Valor Total (UF)",  Integer.class, null);
		
		ComboBox cb = new ComboBox();
		cb.setWidth("150px");
		cb.addItem("Bodega");
		cb.addItem("Estacionamiento");
		
		tableObras.addItem(new Object[]{cb,2,240}, 1);

		tableObras.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el contacto seleccionado?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									;
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});
		
		return tableObras;
    }
}
