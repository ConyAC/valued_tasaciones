package cl.koritsu.valued.view.transactions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import cl.koritsu.valued.domain.ObraComplementaria;
import cl.koritsu.valued.domain.ProgramaBien;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.enums.Adicional;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.domain.enums.Programa;
import cl.koritsu.valued.view.utils.Utils;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.tapio.googlemaps.GoogleMap;
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
import com.vaadin.ui.themes.ValoTheme;

public class EditorSolicitudTasacion extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7296143643420837714L;
	BeanFieldGroup<SolicitudTasacion> bfg = new BeanFieldGroup<SolicitudTasacion>(SolicitudTasacion.class);
	BeanItemContainer<ObraComplementaria> dsObraComplementaria = new BeanItemContainer<ObraComplementaria>(ObraComplementaria.class);
	BeanItemContainer<ProgramaBien> dsProgramaBien = new BeanItemContainer<ProgramaBien>(ProgramaBien.class);
	
    OptionGroup continuar;
    Button btnRegresar = new Button("Regresar");
    Button btnSiguiente = new Button("Guardar");
    Button btnGuardar;
    Component footer;
    VerticalLayout agendar,confirmar,llenar,root,resumen;
    GoogleMap googleMap;
	private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
    Label consoleEntry = new Label();
    
    
    /** CODIGO PARA AGREGAR LISTENER DEL BOTON DE TASACIONES */
    List<OnClickRegresarListener> onClickRegresarEvents = new ArrayList<OnClickRegresarListener>();
    public void addOnClickRegresarEvent(OnClickRegresarListener listener) {
    	onClickRegresarEvents.add(listener);
    }
    private void doClickRegresar(BeanItem<SolicitudTasacion> sol) {
    	for(OnClickRegresarListener listener : onClickRegresarEvents) {
    		listener.onClick(sol);
    	}
    }
	public interface OnClickRegresarListener {
		void onClick(BeanItem<SolicitudTasacion> sol);
	}
	
	/** CODIGO PARA GUARDAR **/
	List<OnClickGuardarListener> onClickGuardarEvents = new ArrayList<OnClickGuardarListener>();
	public void addOnClickGuardarEvent(OnClickGuardarListener listener) {
		onClickGuardarEvents.add(listener);
    }
	
	protected void doClickGuardar(BeanItem<SolicitudTasacion> sol) {
		for(OnClickGuardarListener listener : onClickGuardarEvents) {
    		listener.onClick(sol);
    	}
	}
	public interface OnClickGuardarListener {
		void onClick(BeanItem<SolicitudTasacion> sol);
	}
	

	List<OnClickSiguienteListener> onClickSiguienteEvents = new ArrayList<OnClickSiguienteListener>();
	/**
	 * Agregar un listener para escuchar el evento de siguiente en el formulario. El evento siguiente puede ser de 3 formas:
	 * 
	 *  AGENDADA: Cuando se agenda por primera vez una solicitud
	 *  AGENDADA CON INSIDENCIA: Cuando se agreda por segunda o más veces
	 *  VISITADA: Cuando se confirma la visita al bien
	 *  TASADA: Cuando se termina de agregar la información del bien tasado
	 *  
	 *  El estado se guarda dentro del mismo bean de solicitud en su propiedad estado
	 * @param listener
	 */
    public void addOnClickSiguienteListener(OnClickSiguienteListener listener) {
    	onClickSiguienteEvents.add(listener);
    }
    private void doClickSiguiente(BeanItem<SolicitudTasacion> sol) {
    	for(OnClickSiguienteListener listener : onClickSiguienteEvents) {
    		listener.onClick(sol);
    	}
    }
	public interface OnClickSiguienteListener {
		void onClick(BeanItem<SolicitudTasacion> sol);
	}
	/** FIN CODIGO PARA AGREGAR LISTENER DEL BOTON DE TASACIONES */
    
    
	public EditorSolicitudTasacion() {
		
		setSizeFull();
		
		googleMap = new GoogleMap(apiKey, null, "english");
		
		btnGuardar = buildGuardar();
		
		Panel rootPanel = new Panel();
		rootPanel.setSizeFull();
		
		root = new VerticalLayout();
		rootPanel.setContent(root);
		
		root.setMargin(true);
    	addComponent(rootPanel);
    	setExpandRatio(rootPanel, 1.0f);
	    
		resumen = buildAgregarResumen();
    	root.addComponent(resumen);
	    agendar = buildAgregarVisita();
	    confirmar = buildAgregarConfirmacion();
	    llenar = buildAgregarInfoBien();
	    
    	footer = buildFooter();
        addComponent(footer);
		
	}
	
	private VerticalLayout buildAgregarResumen() {
		VerticalLayout vl = new VerticalLayout();
		vl.setHeightUndefined();
		vl.setWidth("100%");
	 	
      	Label sectionCliente = new Label("Resumen");
      	sectionCliente.addStyleName(ValoTheme.LABEL_H3);
      	sectionCliente.addStyleName(ValoTheme.LABEL_COLORED);	    
	    vl.addComponent(sectionCliente);
	    
	    TextField encargo = new TextField();
	    encargo.setCaption("Fecha Encargo");
	    Utils.bind(bfg, encargo, "fechaEncargoFormateada");
	    vl.addComponent(encargo);
	    
	    TextField cliente = new TextField();
	    cliente.setCaption("Nombre Cliente");
	    Utils.bind(bfg, cliente, "nombreCliente");
	    vl.addComponent(cliente);
	    
	    TextField estado = new TextField();
	    estado.setCaption("Estado");
	    Utils.bind(bfg, estado, "estado");
//	    informe.setValue(solicitud.getTipoInforme().getNombre().toString());
	    vl.addComponent(estado);
	    
	    TextField informe = new TextField();
	    informe.setCaption("Tipo Informe");
	    Utils.bind(bfg, informe, "tipoInformeString");
//	    informe.setValue(solicitud.getTipoInforme().getNombre().toString());
	    vl.addComponent(informe);
	    
	    TextField clase = new TextField();
	    clase.setCaption("Clase Bien");
	    Utils.bind(bfg, clase, "claseBienString");
//	    clase.setValue(solicitud.getBien().getClase().toString());
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
		
	    Label sectionBien = new Label("Agendar Visita");
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
	
	private Button buildGuardar() {
		Button btnGuardar = new Button("Guardar Tasación",FontAwesome.SAVE) ;
		btnGuardar.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//intenda guardar la tasacion
				try {
					bfg.commit();
					//recupera la informacion
					BeanItem<SolicitudTasacion> sol = bfg.getItemDataSource();
					
					//define la lista de obras complementarias extraidas desde la tabla
					if(sol.getBean().getEstado() == EstadoSolicitud.TASADA) {
						sol.getItemProperty("bien.obrasComplementarias").setValue(new HashSet<ObraComplementaria>(dsObraComplementaria.getItemIds()));
						sol.getItemProperty("bien.programas").setValue(new HashSet<ProgramaBien>(dsProgramaBien.getItemIds()));
					}
					
					doClickGuardar(bfg.getItemDataSource());
					Notification.show("Guardado",Type.HUMANIZED_MESSAGE);
					
				}catch(Exception e) {
					e.printStackTrace();
					if(e instanceof CommitException )
						Utils.validateEditor("Error al guardar la tasación", (CommitException) e);
					else
						Notification.show("Error al guardar la tasación",Type.ERROR_MESSAGE);
				}
			}
		});
		return btnGuardar;
	}
	
	/**
	 * 
	 * @return
	 */
	private VerticalLayout buildAgregarInfoBien() {
		VerticalLayout vl = new VerticalLayout();

	    Label sectionBien = new Label("Agregar Información Tasación");
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
		
		Label coor = new Label("Coordenadas");
		coor.addStyleName(ValoTheme.LABEL_H3);
		coor.addStyleName(ValoTheme.LABEL_COLORED);	    
		detailsIngreso.addComponent(coor);
	    
		Panel console = new Panel();
        console.setHeight("100px");
        final CssLayout consoleLayout = new CssLayout();
        console.setContent(consoleLayout);
        detailsIngreso.addComponent(console);
        
        consoleLayout.addComponent(consoleEntry);
		
		return vl;
	}
	

	/**
	 * Permite definir la tasación a mostrar
	 * @param solicitud
	 */
	public void setSolicitud(SolicitudTasacion solicitud) {
        bfg.setItemDataSource(solicitud);
        
        bfg.getField("estado").setReadOnly(true);
        //setea los elementos de los containers
        dsObraComplementaria.removeAllItems();
        dsObraComplementaria.addAll(solicitud.getBien().getObrasComplementarias());
        
        dsProgramaBien.removeAllItems();
        dsProgramaBien.addAll(solicitud.getBien().getProgramas());
        
        definirVista(solicitud.getEstado());
	}
	
	public void setCoordenadas(String coord) {
		consoleEntry.setValue(coord);
	}
	
	/**
	 * Permite definir que formulario se mostrarà
	 * @param estado
	 */
	public void definirVista(EstadoSolicitud estado) {

		//guita el boton guardar si es que existe
		removeComponent(btnGuardar);
		//quita todos los elementos
		root.removeAllComponents();
		//agrega el resumen
		root.addComponent(resumen);
		btnSiguiente.setCaption("Guardar");
		btnSiguiente.removeStyleName(ValoTheme.BUTTON_DANGER);
		switch (estado) {
		case CREADA:
			root.addComponent(agendar, 1);
			break;
		case AGENDADA:
			root.addComponent(confirmar, 1);
			break;
		case AGENDADA_CON_INCIDENCIA:
			root.addComponent(confirmar, 1);
			break;
		case FACTURA:
			//FIXME
			root.addComponent(agendar, 1);
			break;
		case TASADA:
			//agrega el btnGuardar como primer elemento
			addComponent(btnGuardar,0);
			setSpacing(true);
			setComponentAlignment(btnGuardar, Alignment.TOP_LEFT);
			
			root.addComponent(llenar, 1);
			btnSiguiente.setCaption("Enviar a Cliente");
			btnSiguiente.addStyleName(ValoTheme.BUTTON_DANGER);
			break;
		case VISADA:
			//FIXME
			root.addComponent(llenar, 1);
			break;
		case VISADA_CLIENTE:
			//FIXME
			root.addComponent(llenar, 1);
			break;
		case VISITADA:
			//agrega el btnGuardar como primer elemento
			addComponent(btnGuardar,0);
			setSpacing(true);
			setComponentAlignment(btnGuardar, Alignment.TOP_LEFT);
			
			root.addComponent(llenar, 1);
			btnSiguiente.setCaption("Enviar a Visar");
			btnSiguiente.addStyleName(ValoTheme.BUTTON_DANGER);
			break;
		default:
			root.addComponent(agendar, 1);
			break;
		}
	}
	
	 
    /*
     * Permite construir los botones de almacenamiento y regreso a la lista de tasaciones por hacer
     */
    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        btnSiguiente.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSiguiente.setIcon(FontAwesome.SAVE);
        //btnGuadar.focus();
		
        btnRegresar.addStyleName("link");        
        btnRegresar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				//si tiene cambio, confirma de que se perderan
				if(bfg.isModified()) {
					ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de regresar?, se perderán todos los cambios realizados.",
							"Regresar", "Cancelar", new ConfirmDialog.Listener() {

						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								//descarta los cambios
								bfg.discard();
								//ejecuta el evento de regresar
								doClickRegresar(bfg.getItemDataSource());
							}
							//no hace nada
						}
					});
				}else {
					//ejecuta el evento de regresar
					doClickRegresar(bfg.getItemDataSource());
				}
				
			}
		});	
        
	    btnSiguiente.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//dependiendo del estado
				
					BeanItem<SolicitudTasacion> sol = bfg.getItemDataSource();
			    	//verifica que se quiere avanzar de estado en caso de que se envie a visar o al cliente
			    	final boolean porTasar = sol.getBean().getEstado() == EstadoSolicitud.VISITADA;
			    	final boolean porVisar = sol.getBean().getEstado() == EstadoSolicitud.TASADA;
			    	if( porTasar || porVisar ) {
			    		ConfirmDialog.show(UI.getCurrent(),"Confirmar Acción:","Esta a punto de enviar la tasación "+(porTasar ? "para que un visador la revise": "para que se facture")+", ¿quiere continuar?","Enviar", "Cancelar",
			    				new ConfirmDialog.Listener() {
							
							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										bfg.commit();
										//recupera la informacion
										BeanItem<SolicitudTasacion> sol = bfg.getItemDataSource();
										//define el siguiente estado
										sol.getItemProperty("estado").setValue(definirEstado(sol.getBean()));
										
										//define la lista de obras complementarias extraidas desde la tabla
										if(sol.getBean().getEstado() == EstadoSolicitud.TASADA) {
											sol.getItemProperty("bien.obrasComplementarias").setValue(new HashSet<ObraComplementaria>(dsObraComplementaria.getItemIds()));
											sol.getItemProperty("bien.programas").setValue(new HashSet<ProgramaBien>(dsProgramaBien.getItemIds()));
										}
										
										doClickSiguiente(sol);
										Notification.show("Guardado",Type.HUMANIZED_MESSAGE);
									}catch(CommitException e) {
										Utils.validateEditor("", e);
									}catch(Exception e) {
										Notification.show("Ocurrio un error al intentar guardar la solicitud",Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
			    	}
			    	else {
					    try {
							bfg.commit();
							//recupera la informacion
							sol = bfg.getItemDataSource();
							//define el siguiente estado
							sol.getItemProperty("estado").setValue(definirEstado(sol.getBean()));
							
							//define la lista de obras complementarias extraidas desde la tabla
							if(sol.getBean().getEstado() == EstadoSolicitud.TASADA) {
								sol.getItemProperty("bien.obrasComplementarias").setValue(new HashSet<ObraComplementaria>(dsObraComplementaria.getItemIds()));
								sol.getItemProperty("bien.programas").setValue(new HashSet<ProgramaBien>(dsProgramaBien.getItemIds()));
							}
							
							doClickSiguiente(sol);
							Notification.show("Guardado",Type.HUMANIZED_MESSAGE);
						}catch(CommitException e) {
							Utils.validateEditor("", e);
						}catch(Exception e) {
							Notification.show("Ocurrio un error al intentar guardar la solicitud",Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				}
		});
	    
	    footer.addComponent(btnRegresar);
	    footer.addComponent(btnSiguiente);
        footer.setComponentAlignment(btnSiguiente, Alignment.BOTTOM_RIGHT);
        footer.setComponentAlignment(btnRegresar, Alignment.BOTTOM_LEFT);
        
        return footer;
    }
    
    /**
     * De acuerdo al estado y las opciones elegidas en el editor, se define el siguiente estado de la solicitud
     * si la solicitud esta CREADA pasa a AGENDADA
     * si la solicitud esta AGENDADA y se elije NO confirmar la visita pasa a AGENDADA CON INCIDENCIA
     * si la solicitud esta AGENDADA CON INCIDENCIA y se elije NO confirmar la visita se mantiene en AGENDADA CON INCIDENCIA
     * si la solicitud esta AGENDADA o AGENDADA CON INCIDENCIA y se elije SI confirmar la visita pasa a VISITADA
     * si la solicitud esta VISITA pasa a TASADA  
     * si la solicitud esta TASADA pasa a VISADA
     * si la solicitud esta VISADA pasa a VISADA CLIENTE
     * si la solicitud esta VISADA CLIENTE pasa a FACTURADA
     * @param bean
     * @return
     */
    protected EstadoSolicitud definirEstado(SolicitudTasacion bean) {
    	switch(bean.getEstado()){
		case CREADA:
			return EstadoSolicitud.AGENDADA;
		case AGENDADA:
		case AGENDADA_CON_INCIDENCIA:
			if(confirmar.equals("No"))
				return EstadoSolicitud.AGENDADA_CON_INCIDENCIA;
			else
				return EstadoSolicitud.VISITADA;
		case VISITADA:
			return EstadoSolicitud.TASADA;
		case TASADA:
			return EstadoSolicitud.VISADA;
		case VISADA:
			return EstadoSolicitud.VISADA_CLIENTE;
		case VISADA_CLIENTE:
			return EstadoSolicitud.FACTURA;
		case FACTURA:
			return EstadoSolicitud.FACTURA;
    	}
    	return null;
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
	    
		/*
		 * Permite añadir, en la parte inferior del mapa, la historia de las coordenadas por la que
		 * se arrastraron los puntos (draggable)
		 */    
//		googleMap.addMarkerDragListener(new MarkerDragListener() {
//			@Override
//			public void markerDragged(GoogleMapMarker draggedMarker,
//                LatLon oldPosition) {
//                consoleEntry = new Label();
//                consoleEntry.setValue("Marcador arrastrado desde ("
//                    + oldPosition.getLat() + ", " + oldPosition.getLon()
//                    + ") hacia (" + draggedMarker.getPosition().getLat()
//                    + ", " + draggedMarker.getPosition().getLon() + ")");
//                detailsIngreso.addComponent(consoleEntry);
//            }
//        });
		
		
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
}
