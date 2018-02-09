package cl.koritsu.valued.view.transactions;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
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
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.ObraComplementaria;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.enums.Adicional;
import cl.koritsu.valued.domain.enums.Programa;
import cl.koritsu.valued.view.utils.Utils;

public class EditorSolicitudTasacion extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7296143643420837714L;
	BeanFieldGroup<SolicitudTasacion> bfg = new BeanFieldGroup<SolicitudTasacion>(SolicitudTasacion.class);
    OptionGroup continuar;
    FormLayout details, detailsIngreso;
    Button btnRegresar = new Button("Regresar");
    Button btnGuadar = new Button("Aceptar");
    Component footer;
    VerticalLayout formInicial, formIngreso, vlInicial;
    
    
	public EditorSolicitudTasacion() {
		
    	setMargin(true);
    	
    	details = new FormLayout();
       // details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
    	details.setMargin(true);
    	details.setSizeFull();
    	addComponent(details);
    	setExpandRatio(details, 1);
      	
      	Label sectionCliente = new Label("Resumen");
      	sectionCliente.addStyleName(ValoTheme.LABEL_H3);
      	sectionCliente.addStyleName(ValoTheme.LABEL_COLORED);	    
	    details.addComponent(sectionCliente);
	    
	    TextField encargo = new TextField();
	    encargo.setCaption("Fecha Encargo");
	    Utils.bind(bfg, encargo, "fechaEncargoFormateada");
	    details.addComponent(encargo);
	    
	    TextField cliente = new TextField();
	    cliente.setCaption("Nombre Cliente");
	    Utils.bind(bfg, cliente, "nombreCliente");
	    
	    details.addComponent(cliente);
	    Label informe = new Label();
	    informe.setCaption("Tipo Informe");
//	    informe.setValue(solicitud.getTipoInforme().getNombre().toString());
	    details.addComponent(informe);
	    Label clase = new Label();
	    clase.setCaption("Clase Bien");
//	    clase.setValue(solicitud.getBien().getClase().toString());
	    details.addComponent(clase);
	    Label direccion = new Label();
	    direccion.setCaption("Dirección");
//		direccion.setValue(solicitud.getBien().getDireccion() + " " + solicitud.getBien().getNumeroManzana() + ", "
//				+ solicitud.getBien().getComuna().getNombre() + ", "
//				+ solicitud.getBien().getComuna().getRegion().getNombre());
	    details.addComponent(direccion);
	    
        Label sectionBien = new Label("Ingreso");
	    sectionBien.addStyleName(ValoTheme.LABEL_H3);
	    sectionBien.addStyleName(ValoTheme.LABEL_COLORED);	    
	    details.addComponent(sectionBien);
	    
	    PopupDateField fechaVisita = new PopupDateField();
	    bfg.bind(fechaVisita, "fechaTasacion");
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
        
    	addListeners(bfg);
    	
        details.addComponent(continuar);
    	footer = buildFooter();
        addComponent(footer);
		
	}
	
	/**
	 * Permite definir la tasación a mostrar
	 * @param solicitud
	 */
	public void setSolicitud(SolicitudTasacion solicitud) {
        bfg.setItemDataSource(solicitud);
	}
	
	 
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
//				regresar();
			}
		});	
        
        footer.addComponent(btnGuadar);
        footer.addComponent(btnRegresar);
        footer.setComponentAlignment(btnGuadar, Alignment.TOP_LEFT);
        footer.setComponentAlignment(btnRegresar, Alignment.TOP_LEFT);
        
        return footer;
    }
	
    private void addListeners(final BeanFieldGroup<SolicitudTasacion> bfg) {
        
	    continuar.addValueChangeListener(new ValueChangeListener() {
	
	        @Override
	        public void valueChange(ValueChangeEvent event) {
	            if (continuar.isSelected(1)) {
	            	formIngreso = buildFormIngreso(bfg);
	            	removeComponent(footer);
	            	addComponent(formIngreso);
	            } else if (continuar.isSelected(2)) {
	            	removeComponent(formIngreso);
	            	addComponent(footer);
	            }
	        }
	    });
	    
	    btnGuadar.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					bfg.commit();
					SolicitudTasacion sol = bfg.getItemDataSource().getBean();
//					service.saveSolicitud(sol);
					Notification.show("Guardado",Type.HUMANIZED_MESSAGE);
				}catch(CommitException e) {
					Utils.validateEditor("", e);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
    
    public VerticalLayout buildFormIngreso(BeanFieldGroup<SolicitudTasacion> bfg) {
    	VerticalLayout vl = new VerticalLayout();
    	//vl.setMargin(true);
    	
    	detailsIngreso = new FormLayout();
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

	    final Table tableObras = buildTableObras(bfg.getItemDataSource().getBean().getBien().getId());
	    
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
    private Table buildTableObras(long bienId) {
		final Table tableObras = new Table();
		tableObras.setHeight("100px");
		
		BeanItemContainer<ObraComplementaria> ds = new BeanItemContainer<ObraComplementaria>(ObraComplementaria.class);
		//List<ObraComplementaria> obrascomplementarias = service.findObrasComplementariasByBien(bienId);
		//ds.addAll(obrascomplementarias);
		
		tableObras.setBuffered(true);
		
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
									source.removeItem(itemId);
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});
		
		tableObras.setContainerDataSource(ds);
		tableObras.setEditable(true);
		tableObras.setVisibleColumns("adicional","cantidadSuperficie","valorTotalUF","eliminar");
		
		return tableObras;
    }
}
