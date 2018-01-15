package cl.koritsu.valued.view.transactions;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.Comuna;
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
import cl.koritsu.valued.view.utils.Utils;
import ru.xpoft.vaadin.VaadinView;

@SuppressWarnings({ "serial", "unchecked" })
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = MisSolicitudesView.NAME, cached = true)
public final class MisSolicitudesView extends VerticalLayout implements View {
	
	public static final String NAME = "en_proceso";

	FilterTable table;
	FormLayout details, detailsIngreso;
    Label consoleEntry;
    OptionGroup continuar;
    VerticalLayout formInicial, formIngreso, vlInicial;
    Component footer;
    GoogleMap googleMap;
    private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
    private static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
    private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
            "theater", "room", "title", "seats" };

    @Autowired
    ValuedService service;
    
    BeanItemContainer<SolicitudTasacion> solicitudContainer = new BeanItemContainer<SolicitudTasacion>(SolicitudTasacion.class);
 
    Window mapToolBox = new Window();

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
       
        mapToolBox.setClosable(false);
        mapToolBox.setResizable(true);
        mapToolBox.setPosition(210, 220);
        //FIXME
        mapToolBox.setWidth("550px");
        mapToolBox.setHeight("520px");
        //mapToolBox.addStyleName("mywindowstyle");
        
             
        //situamos, inicialmente, el mapa en Santiago.
      	googleMap.setCenter(new LatLon(-33.448779, -70.668551));
		
        table = buildTable();
        mapsPanel.setContent(googleMap);
        
    	footer = buildFooter();

    	mapToolBox.setContent(table);
        mapToolBox.setData("no_cerrar");
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

        HorizontalLayout tools = new HorizontalLayout(buildFilter());
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);
        
        return header;
    }
    
    /*
     * Cambiar filtro segun perfil
     */
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

                        return filterByProperty("numeroTasacion", item,
                                event.getText())
                                || filterByProperty("estado", item,
                                        event.getText())
                                || filterByProperty("bien.direccion", item,
                                        event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("numeroTasacion")
                                || propertyId.equals("estado")
                                || propertyId.equals("bien.direccion")) {
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
    
    private FilterTable buildTable() {
    	FilterTable table = new FilterTable()
    	{
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
    	table.setImmediate(true);
        table.setContainerDataSource(solicitudContainer);
        table.setSizeFull();
//        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
//        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
//        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setSelectable(true);
        table.setColumnReorderingAllowed(true);
        table.setSortAscending(false);
        
        table.addGeneratedColumn("nombrecliente", new CustomTable.ColumnGenerator() {

			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId)).getBean();
				return sol.getCliente() != null ? sol.getCliente().getNombreCliente() : "";
			}
		});
        
        table.addGeneratedColumn("direccion", new CustomTable.ColumnGenerator() {

			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId)).getBean();
				Bien bien = sol.getBien();				
				return (bien != null && bien.getDireccion() != null)
						? bien.getDireccion() + " " + bien.getNumeroManzana() + " " + bien.getComuna().getNombre() + " "
								+ bien.getComuna().getRegion().getNombre()
						: "";
			}
		});

        table.addGeneratedColumn("acceder", new CustomTable.ColumnGenerator()  {

			@Override
			public Object generateCell(final CustomTable source, final Object itemId, Object columnId) {
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
						googleMap.setZoom(20);
						
						if(sol.getBien() != null && sol.getBien().getComuna() != null)
							cargarTasaciones(sol.getBien().getComuna());
						
						mapToolBox.setContent(buildForm(sol));
					}
				});
				return editarTasacion;
			}
        });
        
        table.setVisibleColumns("acceder","numeroTasacion","fechaEncargo","direccion");
        table.setColumnHeaders("Acceder","N° Tasación", "Fecha Encargo","Dirección");
        table.setFilterBarVisible(true);
        table.setFooterVisible(true);

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
    	comuna.setId(13101L);
    	List<SolicitudTasacion> solicitudes = service.getTasacionesByRegionAndComuna(comuna);    	
    	((BeanItemContainer<SolicitudTasacion>)table.getContainerDataSource()).addAll(solicitudes);   	
    	
    }
    
    /*
     * Permite crear el formulario de ingreso para el tasador.
     */
    public VerticalLayout buildForm(final SolicitudTasacion solicitud) {
    	vlInicial = new VerticalLayout();
    	vlInicial.setMargin(true);
    	
    	details = new FormLayout();
       // details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
    	details.setMargin(true);
    	details.setSizeFull();
    	vlInicial.addComponent(details);
    	vlInicial.setExpandRatio(details, 1);
      	
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
	    
	    PopupDateField fechaVisita = new PopupDateField();
	    fechaVisita.setCaption("Fecha Visita");
        details.addComponent(fechaVisita);
        
	    TextArea incidencia = new TextArea("Incidencia");
	    incidencia.setRows(10);
	    incidencia.setWordwrap(false);
        details.addComponent(incidencia);
        
        continuar = new OptionGroup("¿Continuar Ingreso?");
        continuar.addItem(1);
        continuar.addItem(2);
        continuar.setItemCaption(1, "Si");
        continuar.setItemCaption(2, "No");        
        continuar.setImmediate(true);
        continuar.addStyleName("horizontal");
        
    	addListeners();
    	
        details.addComponent(continuar);
        
	  /*  HorizontalLayout hl = new HorizontalLayout();
	    details.addComponent(hl);
	    TextField superTerreno = new TextField();
	    superTerreno.setCaption("Mts Superficie Terreno");
        hl.addComponent(superTerreno);
	    TextField valorSuperTerreno = new TextField();
	    valorSuperTerreno.setCaption("Valor Mts Superficie Terreno");
        hl.addComponent(valorSuperTerreno);
        
	    HorizontalLayout hl2 = new HorizontalLayout();
	    details.addComponent(hl2);
	    TextField superEdif = new TextField();
	    superEdif.setCaption("Mts Superficie Edificado");
        hl2.addComponent(superEdif);
	    TextField valorSuperEdif = new TextField();
	    valorSuperEdif.setCaption("Valor Mts Superficie Edificado");
        hl2.addComponent(valorSuperEdif);
        
	    HorizontalLayout hl3 = new HorizontalLayout();
	    details.addComponent(hl3);
	    TextField superficieBalcon = new TextField("Superficie Balcón/Terraza");
	    hl3.addComponent(superficieBalcon);
	    TextField superficieTerraza = new TextField("Valor UF Balcón/Terraza");
	    hl3.addComponent(superficieTerraza);
        
        Label obras = new Label("Adicionales");
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
		/*googleMap.addMarkerDragListener(new MarkerDragListener() {
			@Override
			public void markerDragged(GoogleMapMarker draggedMarker,
                LatLon oldPosition) {
                consoleEntry = new Label();
                consoleEntry.setValue("Marcador arrastrado desde ("
                    + oldPosition.getLat() + ", " + oldPosition.getLon()
                    + ") hacia (" + draggedMarker.getPosition().getLat()
                    + ", " + draggedMarker.getPosition().getLon() + ")");
                details.addComponent(consoleEntry);
            }
        });
		*/
        vlInicial.addComponent(footer);
		
	    return vlInicial;
    }
    
    
    private void addListeners() {
    
	    continuar.addValueChangeListener(new ValueChangeListener() {
	
	        @Override
	        public void valueChange(ValueChangeEvent event) {
	            if (continuar.isSelected(1)) {
	            	formIngreso = buildFormIngreso();
	            	vlInicial.removeComponent(footer);
	            	vlInicial.addComponent(formIngreso);
	            } else if (continuar.isSelected(2)) {
	            	vlInicial.removeComponent(formIngreso);
	            	vlInicial.addComponent(footer);
	            }
	        }
	    });
    }
    
    public VerticalLayout buildFormIngreso() {
    	VerticalLayout vl = new VerticalLayout();
    	//vl.setMargin(true);
    	
    	detailsIngreso = new FormLayout();
       // details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
    	//detailsIngreso.setMargin(true);
    	detailsIngreso.setSizeFull();
        vl.addComponent(detailsIngreso);
       // vl.setExpandRatio(detailsIngreso, 1);
        
	    HorizontalLayout hl = new HorizontalLayout();
	    detailsIngreso.addComponent(hl);
	    TextField superTerreno = new TextField();
	    superTerreno.setCaption("Mts Superficie Terreno");
        hl.addComponent(superTerreno);
	    TextField valorSuperTerreno = new TextField();
	    valorSuperTerreno.setCaption("Valor Mts Superficie Terreno");
        hl.addComponent(valorSuperTerreno);
        
	    HorizontalLayout hl2 = new HorizontalLayout();
	    detailsIngreso.addComponent(hl2);
	    TextField superEdif = new TextField();
	    superEdif.setCaption("Mts Superficie Edificado");
        hl2.addComponent(superEdif);
	    TextField valorSuperEdif = new TextField();
	    valorSuperEdif.setCaption("Valor Mts Superficie Edificado");
        hl2.addComponent(valorSuperEdif);
        
	    HorizontalLayout hl3 = new HorizontalLayout();
	    detailsIngreso.addComponent(hl3);
	    TextField superficieBalcon = new TextField("Superficie Balcón/Terraza");
	    hl3.addComponent(superficieBalcon);
	    TextField superficieTerraza = new TextField("Valor UF Balcón/Terraza");
	    hl3.addComponent(superficieTerraza);
        
        Label obras = new Label("Adicionales");
        obras.addStyleName(ValoTheme.LABEL_H3);
        obras.addStyleName(ValoTheme.LABEL_COLORED);	    
        detailsIngreso.addComponent(obras);
	    
	    Button btnObras = new Button(null,FontAwesome.PLUS);
	    detailsIngreso.addComponent(btnObras);
//		btnObras.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				;				
//			}
//		});	

	    
	    detailsIngreso.addComponent(buildTableObras());	  
		
		Label programa = new Label("Programa");
		programa.addStyleName(ValoTheme.LABEL_H3);
		programa.addStyleName(ValoTheme.LABEL_COLORED);	    
		detailsIngreso.addComponent(programa);
	    
	    Button btnPrograma = new Button(null,FontAwesome.PLUS);
	    detailsIngreso.addComponent(btnPrograma);
//		btnPrograma.addClickListener(new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				;				
//			}
//		});	
		
	    detailsIngreso.addComponent(buildTablePrograma());
		
		Label coor = new Label("Coordenadas");
		coor.addStyleName(ValoTheme.LABEL_H3);
		coor.addStyleName(ValoTheme.LABEL_COLORED);	    
		detailsIngreso.addComponent(coor);
	    
		/*
		 * Permite añadir, en la parte inferior del mapa, la historia de las coordenadas por la que
		 * se arrastraron los puntos (draggable)
		 */    
		googleMap.addMarkerDragListener(new MarkerDragListener() {
			@Override
			public void markerDragged(GoogleMapMarker draggedMarker,
                LatLon oldPosition) {
                consoleEntry = new Label();
                consoleEntry.setValue("Marcador arrastrado desde ("
                    + oldPosition.getLat() + ", " + oldPosition.getLon()
                    + ") hacia (" + draggedMarker.getPosition().getLat()
                    + ", " + draggedMarker.getPosition().getLon() + ")");
                detailsIngreso.addComponent(consoleEntry);
            }
        });
		
		vl.addComponent(footer);
		
	    return vl;
    }
    
    Button btnRegresar = new Button("Regresar");
    Button btnGuadar = new Button("Aceptar");
    
    /*
     * Permite construir los botones de almacenamiento y regreso a la lista de tasaciones por hacer
     */
    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        btnGuadar.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnGuadar.setIcon(FontAwesome.SAVE);
        //btnGuadar.focus();
		
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
    
    /*
     * Permite crear la tabla del programa.
     */
    private VerticalLayout buildTablePrograma() {
    	VerticalLayout vll = new VerticalLayout();
 
    	final Table tablePrograma = new Table();
		tablePrograma.setHeight("100px");
		tablePrograma.addContainerProperty("Elementos", ComboBox.class, null);
		tablePrograma.addContainerProperty("Cantidad/Superficie",  Integer.class, null);
		
		ComboBox cbProg = new ComboBox();
		cbProg.setWidth("150px");
		cbProg.setNullSelectionAllowed(false);
		for(Programa p : Programa.values()){
			cbProg.addItem(p);
		}
		
		tablePrograma.addItem(new Object[]{cbProg,2}, 1);

		tablePrograma.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el programa seleccionado?",
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
    
    /*
     * Permite crear la tabla de obras complementarias
     */
    private Table buildTableObras() {
		final Table tableObras = new Table();
		tableObras.setHeight("100px");
		tableObras.addContainerProperty("Elemento", ComboBox.class, null);
		tableObras.addContainerProperty("Cantidad/Superficie",  Integer.class, null);
		tableObras.addContainerProperty("Valor Total (UF)",  Integer.class, null);
		
		ComboBox cb = new ComboBox();
		cb.setWidth("150px");
		cb.setNullSelectionAllowed(false);
		for(Adicional p : Adicional.values()){
			cb.addItem(p);
		}
		
		tableObras.addItem(new Object[]{cb,2,240}, 1);

		tableObras.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el adicional seleccionado?",
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
