package cl.koritsu.valued.view.utils;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.ObraComplementaria;
import cl.koritsu.valued.domain.ProgramaBien;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.enums.Adicional;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.domain.enums.EtapaTasacion;
import cl.koritsu.valued.domain.enums.Programa;
import cl.koritsu.valued.services.ValuedService;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerDragListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class EditarSolicitudTasacion extends VerticalLayout {


	/**
	 * 
	 */
	private static final long serialVersionUID = -816834622774466133L;
	
	BeanFieldGroup<SolicitudTasacion> bfg = new BeanFieldGroup<SolicitudTasacion>(SolicitudTasacion.class);
	BeanItemContainer<ObraComplementaria> dsObraComplementaria = new BeanItemContainer<ObraComplementaria>(ObraComplementaria.class);
	BeanItemContainer<ProgramaBien> dsProgramaBien = new BeanItemContainer<ProgramaBien>(ProgramaBien.class);
	
	@Autowired
	ValuedService service;
    OptionGroup continuar;
    Button btnRegresar = new Button("Regresar");
    Button btnGuadar = new Button("Guardar");
    Component footer;
    VerticalLayout agendar,confirmar,llenar,root,resumen;
    GoogleMap googleMap;
	private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
	boolean editar;
	Window window;
	SolicitudTasacion solicitudTasacion;
	MapaTasacion mapaTasacion;
	
	public EditarSolicitudTasacion(ValuedService service, SolicitudTasacion solicitud, Window win) {
		
		this.service = service;		
		this.window = win;
		this.solicitudTasacion = solicitud;
		
		bfg.setItemDataSource(solicitud);
		setDataBienSolicitud(solicitud);
	        
		setSizeFull();
		
		googleMap = new GoogleMap(apiKey, null, "english");
		
		Panel rootPanel = new Panel();
		rootPanel.setSizeFull();
		
		mapaTasacion = new MapaTasacion(service, googleMap);
		
		root = new VerticalLayout();
		rootPanel.setContent(root);
		
		root.setMargin(true);
    	addComponent(rootPanel);
    	setExpandRatio(rootPanel, 1.0f);
	    
		resumen = buildAgregarResumen();
    	root.addComponent(resumen);
    	
	    agendar = buildAgregarVisita();
	    root.addComponent(agendar);
	    
//	    confirmar = buildAgregarConfirmacion();
//	    root.addComponent(confirmar);
	    
	    llenar = buildAgregarInfoBien();
	    root.addComponent(llenar);
    	
    	footer = buildFooter();
        addComponent(footer);
		
	}
	
	private VerticalLayout buildAgregarResumen() {
		VerticalLayout vl = new VerticalLayout();
		vl.setHeightUndefined();
		vl.setWidth("100%");
	 	
      	Label sectionCliente = new Label("Editar Información Básica");
      	sectionCliente.addStyleName(ValoTheme.LABEL_H3);
      	sectionCliente.addStyleName(ValoTheme.LABEL_COLORED);	    
	    vl.addComponent(sectionCliente);
	    
	    TextField encargo = new TextField();
	    encargo.setCaption("Fecha Encargo");
	    Utils.bind(bfg, encargo, "fechaEncargoFormateada");
	    vl.addComponent(encargo);
		
	    BeanItemContainer<Cliente> cls = new BeanItemContainer<Cliente>(Cliente.class);
		List<Cliente> clientes = service.getClientes();
		cls.addAll(clientes);
		
	    ComboBox cbCliente = new ComboBox();
	    cbCliente.setCaption("Nombre Cliente");
	    cbCliente.setContainerDataSource(cls);
	    cbCliente.setImmediate(true);
	    cbCliente.setItemCaptionMode(ItemCaptionMode.ID);
	    cbCliente.setItemCaptionPropertyId("nombreCliente");
	    Utils.bind(bfg, cbCliente, "cliente");
	    vl.addComponent(cbCliente);
	    
	    TextField estado = new TextField();
	    estado.setCaption("Estado");
	    Utils.bind(bfg, estado, "estado");
	    vl.addComponent(estado);
	    
	    TextField informe = new TextField();
	    informe.setCaption("Tipo Informe");
	    Utils.bind(bfg, informe, "tipoInformeString");
	    vl.addComponent(informe);
	    
	    TextField clase = new TextField();
	    clase.setCaption("Clase Bien");
	    Utils.bind(bfg, clase, "claseBienString");
	    vl.addComponent(clase);
	    
	    TextArea direccion = new TextArea();
	    direccion.setWidth("100%");
	    direccion.setRows(2);
	    Utils.bind(bfg, direccion, "direccionCompleta");
	    direccion.setCaption("Dirección");
	    vl.addComponent(direccion);
	    
		return vl;
	}
	
	private VerticalLayout buildAgregarVisita() {
		VerticalLayout vl = new VerticalLayout();
		vl.setHeightUndefined();
		vl.setWidth("100%");
		
	    Label sectionBien = new Label("Fecha de Visita");
	    sectionBien.addStyleName(ValoTheme.LABEL_H3);
	    sectionBien.addStyleName(ValoTheme.LABEL_COLORED);	    
	    vl.addComponent(sectionBien);
		
		PopupDateField fechaVisita = new PopupDateField();
		fechaVisita.setDateFormat("dd/MM/yyyy");
	    bfg.bind(fechaVisita, "fechaTasacion");
	    fechaVisita.setCaption("Fecha Visita");
        vl.addComponent(fechaVisita);        
	    
		return vl;
	}
	
	/**
	 * 
	 * @return
	 */
	private VerticalLayout buildAgregarConfirmacion() {
		VerticalLayout vl = new VerticalLayout();
		
	    Label sectionBien = new Label("Confirmar Visita");
	    sectionBien.addStyleName(ValoTheme.LABEL_H3);
	    sectionBien.addStyleName(ValoTheme.LABEL_COLORED);	    
	    vl.addComponent(sectionBien);
		
		final OptionGroup continuar = new OptionGroup("¿Se llevó a cabo la visita en la fecha ingresada?");
        continuar.addItem(1);
        continuar.addItem(2);
        continuar.setItemCaption(1, "Si");
        continuar.setItemCaption(2, "No");        
        continuar.setImmediate(true);
        continuar.addStyleName("horizontal");
        
        vl.addComponent(continuar);
		
		final TextArea incidencia = new TextArea("Incidencia");
		incidencia.setVisible(false);
	    incidencia.setRows(10);
	    incidencia.setWordwrap(false);
        vl.addComponent(incidencia);
        
	    continuar.addValueChangeListener(new ValueChangeListener() {
	    	
	        @Override
	        public void valueChange(ValueChangeEvent event) {
            	incidencia.setVisible(!continuar.isSelected(1));
	        }
	    });
        
		return vl;
	}
	
	/**
	 * 
	 * @return
	 */
	private VerticalLayout buildAgregarInfoBien() {
		VerticalLayout vl = new VerticalLayout();


	    Label sectionBien = new Label("Información Tasación");
	    sectionBien.addStyleName(ValoTheme.LABEL_H3);
	    sectionBien.addStyleName(ValoTheme.LABEL_COLORED);	    
	    vl.addComponent(sectionBien);
    	
		final FormLayout detailsIngreso = new FormLayout();
       // details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
    	//detailsIngreso.setMargin(true);
    	detailsIngreso.setSizeFull();
        vl.addComponent(detailsIngreso);
       // vl.setExpandRatio(detailsIngreso, 1);
        
	    HorizontalLayout hl = new HorizontalLayout();
	    hl.setSpacing(true);
	    detailsIngreso.addComponent(hl);
	    TextField superTerreno = new TextField();
	    superTerreno.setCaption("Mts Superficie Terreno");
	    Utils.bind(bfg, superTerreno, "bien.superficieTerreno");
        hl.addComponent(superTerreno);
	    TextField valorSuperTerreno = new TextField();
	    valorSuperTerreno.setCaption("Valor UF Superficie Terreno");
	    Utils.bind(bfg, valorSuperTerreno, "bien.ufSuperficieTerreno");
        hl.addComponent(valorSuperTerreno);
        
	    HorizontalLayout hl2 = new HorizontalLayout();
	    hl2.setSpacing(true);
	    detailsIngreso.addComponent(hl2);
	    TextField superEdif = new TextField();
	    superEdif.setCaption("Mts Superficie Edificado");
	    Utils.bind(bfg, superEdif, "bien.superficieConstruida");
        hl2.addComponent(superEdif);
	    TextField valorSuperEdif = new TextField();
	    valorSuperEdif.setCaption("Valor UF Superficie Edificado");
	    Utils.bind(bfg, valorSuperEdif, "bien.ufSuperficieConstruida");
        hl2.addComponent(valorSuperEdif);
        
	    HorizontalLayout hl3 = new HorizontalLayout();
	    hl3.setSpacing(true);
	    detailsIngreso.addComponent(hl3);
	    TextField superficieBalcon = new TextField("Superficie Balcón/Terraza");
	    Utils.bind(bfg, superficieBalcon, "bien.superficieTerraza");
	    hl3.addComponent(superficieBalcon);
	    TextField  valorSuperficieBalcon = new TextField("Valor UF Balcón/Terraza");
	    Utils.bind(bfg, valorSuperficieBalcon, "bien.ufSuperficieTerraza");
	    hl3.addComponent(valorSuperficieBalcon);
        
        Label obras = new Label("Adicionales");
        obras.addStyleName(ValoTheme.LABEL_H3);
        obras.addStyleName(ValoTheme.LABEL_COLORED);	    
        detailsIngreso.addComponent(obras);
	    
	    Button btnObras = new Button(null,FontAwesome.PLUS);
	    detailsIngreso.addComponent(btnObras);

	    final Table tableObras = buildTableObras();
	    
	    Utils.bind(bfg, tableObras, "bien.obrasComplementarias");
	    
		btnObras.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ObraComplementaria obra = new ObraComplementaria();
				obra.setAdicional(Adicional.ESTACIONAMIENTO);
				obra.setBien(bfg.getItemDataSource().getBean().getBien());
				dsObraComplementaria.addBean(obra);
			}
		});	
		
	    detailsIngreso.addComponent(tableObras);	  
		
		Label programa = new Label("Programa");
		programa.addStyleName(ValoTheme.LABEL_H3);
		programa.addStyleName(ValoTheme.LABEL_COLORED);	    
		detailsIngreso.addComponent(programa);
	    
	    Button btnPrograma = new Button(null,FontAwesome.PLUS);
	    detailsIngreso.addComponent(btnPrograma);
		btnPrograma.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ProgramaBien programa = new ProgramaBien();
				programa.setPrograma(Programa.BAÑO);
				programa.setBien(bfg.getItemDataSource().getBean().getBien());
				dsProgramaBien.addBean(programa);			
			}
		});	
		
	    detailsIngreso.addComponent(buildTablePrograma());
		
		detailsIngreso.addComponent(buildSeccionCoordenadas());
		
		return vl;
	}
	 
    /*
     * Permite construir los botones de almacenamiento 
     */
    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        btnGuadar.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnGuadar.setIcon(FontAwesome.SAVE);
        //btnGuadar.focus();
        
	    btnGuadar.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//dependiendo del estado
				try {
					bfg.commit();
					//recupera la informacion
					BeanItem<SolicitudTasacion> sol = bfg.getItemDataSource();
					
					//define la lista de obras complementarias extraidas desde la tabla
					if(sol.getBean().getEstado() == EstadoSolicitud.TASADA) {
						sol.getItemProperty("bien.obrasComplementarias").setValue(new HashSet<ObraComplementaria>(dsObraComplementaria.getItemIds()));
						sol.getItemProperty("bien.programas").setValue(new HashSet<ProgramaBien>(dsProgramaBien.getItemIds()));
					}					
					
					service.saveSolicitud(sol.getBean());
					service.saveBitacora(sol.getBean(), EtapaTasacion.VISAR);
					((UI)window.getParent()).removeWindow(window);
						
					Notification.show("Guardado",Type.HUMANIZED_MESSAGE);
				}catch(CommitException e) {
					Utils.validateEditor("", e);
				}catch(Exception e) {
					Notification.show("Ocurrio un error al intentar guardar la solicitud",Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

	    footer.addComponent(btnGuadar);
        footer.setComponentAlignment(btnGuadar, Alignment.BOTTOM_RIGHT);
        
        return footer;
    }
    

    /**
     * 
     * @param bfg
     * @return
     */
	public VerticalLayout buildFormIngreso(BeanFieldGroup<SolicitudTasacion> bfg) {
    	VerticalLayout vl = new VerticalLayout();
    	//vl.setMargin(true);
    	
    	FormLayout detailsIngreso = new FormLayout();
       // details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
    	//detailsIngreso.setMargin(true);
    	detailsIngreso.setSizeFull();
        vl.addComponent(detailsIngreso);
       // vl.setExpandRatio(detailsIngreso, 1);
        
	    HorizontalLayout hl = new HorizontalLayout();
	    hl.setSpacing(true);
	    detailsIngreso.addComponent(hl);
	    TextField superTerreno = new TextField();
	    superTerreno.setCaption("Mts Superficie Terreno");
	    Utils.bind(bfg, superTerreno, "bien.superficieTerreno");
        hl.addComponent(superTerreno);
        
	    TextField valorSuperTerreno = new TextField();
	    valorSuperTerreno.setCaption("Valor Mts Superficie Terreno");
        hl.addComponent(valorSuperTerreno);
        
	    HorizontalLayout hl2 = new HorizontalLayout();
	    hl2.setSpacing(true);
	    detailsIngreso.addComponent(hl2);
	    TextField superEdif = new TextField();
	    Utils.bind(bfg, superEdif, "bien.superficieConstruida");
	    superEdif.setCaption("Mts Superficie Edificado");
        hl2.addComponent(superEdif);
	    TextField valorSuperEdif = new TextField();
	    valorSuperEdif.setCaption("Valor Mts Superficie Edificado");
        hl2.addComponent(valorSuperEdif);
        
	    HorizontalLayout hl3 = new HorizontalLayout();
	    hl3.setSpacing(true);
	    detailsIngreso.addComponent(hl3);
	    TextField superficieBalcon = new TextField("Superficie Balcón/Terraza");
	    Utils.bind(bfg, superficieBalcon, "bien.superficieTerraza");
	    hl3.addComponent(superficieBalcon);
	    TextField superficieTerraza = new TextField("Valor UF Balcón/Terraza");
	    hl3.addComponent(superficieTerraza);
        
        Label obras = new Label("Adicionales");
        obras.addStyleName(ValoTheme.LABEL_H3);
        obras.addStyleName(ValoTheme.LABEL_COLORED);	    
        detailsIngreso.addComponent(obras);
	    
	    Button btnObras = new Button(null,FontAwesome.PLUS);
	    detailsIngreso.addComponent(btnObras);

	    final Table tableObras = buildTableObras();
	    
	    Utils.bind(bfg, tableObras, "bien.obrasComplementarias");
	    
		btnObras.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ObraComplementaria obra = new ObraComplementaria();
				obra.setAdicional(Adicional.ESTACIONAMIENTO);
				((BeanItemContainer<ObraComplementaria>) tableObras.getContainerDataSource()).addBean(obra);
								
			}
		});	
		
	    detailsIngreso.addComponent(tableObras);	  
		
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
		
	    return vl;
    }
    
    /*
     * Permite crear la tabla del programa.
     */
    private VerticalLayout buildTablePrograma() {
    	VerticalLayout vll = new VerticalLayout();
 
    	final Table tablePrograma = new Table();    	
    	tablePrograma.setContainerDataSource(dsProgramaBien);   	
		tablePrograma.setHeight("100px");
		
		tablePrograma.setTableFieldFactory(new TableFieldFactory() {
			
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				Field<?> field = null; 
				if(propertyId.equals("cantidadSuperficie")){
					field = new TextField();
					((TextField)field).setImmediate(true);
				} else if(  propertyId.equals("programa") ){
						field = new ComboBox();
						field.setWidth("150px");
						((ComboBox)field).setNullSelectionAllowed(false);
						for(Programa p : Programa.values()){
							((ComboBox)field).addItem(p);
						}
				} else if( propertyId.equals("eliminar")) {
					return null;
				}
				
				field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
					
				return field;
			}
		});	
		

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
									dsProgramaBien.removeItem(itemId);
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});
		
    	Utils.bind(bfg, tablePrograma, "bien.programas");
		
		tablePrograma.setEditable(true);
		tablePrograma.setVisibleColumns("programa","cantidadSuperficie","eliminar");
		tablePrograma.setColumnHeaders("Programa","Cant. Superficie","Eliminar");
		
		vll.addComponent(tablePrograma);
		vll.setComponentAlignment(tablePrograma, Alignment.MIDDLE_LEFT);
		return vll;
    }
    
    /**
     * Permite crear la tabla de obras complementarias
     */
    private Table buildTableObras() {
		final Table tableObras = new Table();
		tableObras.setHeight("100px");

		tableObras.setTableFieldFactory(new TableFieldFactory() {
			
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				Field<?> field = null; 
				if(propertyId.equals("cantidadSuperficie") || propertyId.equals("valorTotalUF") ){
					field = new TextField();
					((TextField)field).setImmediate(true);
				} else if(  propertyId.equals("adicional") ){
						field = new ComboBox();
						field.setWidth("150px");
						((ComboBox)field).setNullSelectionAllowed(false);
						for(Adicional p : Adicional.values()){
							((ComboBox)field).addItem(p);
						}
				} else if( propertyId.equals("eliminar")) {
					return null;
				}
				
				field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
					
				return field;
			}
		});	
		
		tableObras.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el adicional seleccionado?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									dsObraComplementaria.removeItem(itemId);
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});
		
		tableObras.setContainerDataSource(dsObraComplementaria);
		tableObras.setEditable(true);
		tableObras.setVisibleColumns("adicional","cantidadSuperficie","valorTotalUF","eliminar");
		tableObras.setColumnHeaders("Adicional","Cant. Superficie","Total UF","Eliminar");
		
		return tableObras;
    }
    
    private VerticalLayout buildSeccionCoordenadas(){
    	VerticalLayout vl = new VerticalLayout();
    	
    	final FormLayout detailsIngreso = new FormLayout();
     	detailsIngreso.setSizeFull();
         vl.addComponent(detailsIngreso);
         
    	Label coor = new Label("Coordenadas");
		coor.addStyleName(ValoTheme.LABEL_H3);
		coor.addStyleName(ValoTheme.LABEL_COLORED);	    
		detailsIngreso.addComponent(coor);
		
		detailsIngreso.addComponent(buildMapa());
	    
		Panel console = new Panel();
        console.setHeight("100px");
        final CssLayout consoleLayout = new CssLayout();
        console.setContent(consoleLayout);
        detailsIngreso.addComponent(console);
		/*
		 * Permite añadir, en la parte inferior del mapa, la historia de las coordenadas por la que
		 * se arrastraron los puntos (draggable)
		 */    
		googleMap.addMarkerDragListener(new MarkerDragListener() {
			@Override
			public void markerDragged(GoogleMapMarker draggedMarker,
                LatLon oldPosition) {
                Label consoleEntry = new Label();
                TextField lat = new TextField();
                TextField lon = new TextField();
                consoleEntry.setValue("Marcador arrastrado desde ("
                    + oldPosition.getLat() + ", " + oldPosition.getLon()
                    + ") hacia (" + draggedMarker.getPosition().getLat()
                    + ", " + draggedMarker.getPosition().getLon() + ")");
                consoleLayout.addComponent(consoleEntry);
                lat.setValue(draggedMarker.getPosition().getLat()+"");
                lon.setValue(draggedMarker.getPosition().getLon()+"");
                
                Utils.bind(bfg, lat, "norteY");
                Utils.bind(bfg, lon, "esteX");
            }
        });
		
		return vl;
    }
    
    private VerticalLayout buildMapa(){		
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
        		
		googleMap = new GoogleMap(apiKey, null, "english");
		//situamos, inicialmente, el mapa en Santiago.
		googleMap.setCenter(new LatLon(-33.448779, -70.668551));
		googleMap.setSizeFull();	
				
		googleMap.setMinZoom(4);
		googleMap.setMaxZoom(16);
		
		googleMap.setZoom(20);
		
		double lat = solicitudTasacion.getNorteY();
		double lon = solicitudTasacion.getEsteX();
		
		if(lat == 0 || lon == 0){
			LatLon coordenadas = mapaTasacion.recuperarCoordenadas(solicitudTasacion);
			if(coordenadas.getLat() > 0 && coordenadas.getLon() > 0){
				googleMap.setCenter(new LatLon(coordenadas.getLat(),coordenadas.getLon()));
				googleMap.addMarker(solicitudTasacion.getNumeroTasacion(), new LatLon(coordenadas.getLat(), coordenadas.getLon()), true, "VAADIN/img/pin_tas_asignada.png");
			}
		}else{
			googleMap.setCenter(new LatLon(lat,lon));
			googleMap.addMarker(solicitudTasacion.getNumeroTasacion(), new LatLon(solicitudTasacion.getNorteY(),solicitudTasacion.getEsteX()), true, "VAADIN/img/pin_tas_asignada.png");
		}

		vl.addComponent(googleMap);
		
		return vl;
	}
    
    public void setDataBienSolicitud(SolicitudTasacion solicitud) {
      
        dsObraComplementaria.removeAllItems();
        dsObraComplementaria.addAll(solicitud.getBien().getObrasComplementarias());
        
        dsProgramaBien.removeAllItems();
        dsProgramaBien.addAll(solicitud.getBien().getProgramas());
        
	}
}
