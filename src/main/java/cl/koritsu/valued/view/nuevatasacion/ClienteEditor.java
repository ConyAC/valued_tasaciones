package cl.koritsu.valued.view.nuevatasacion;

import java.util.List;
import java.util.Locale;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.Cargo;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Contacto;
import cl.koritsu.valued.domain.RazonSocial;
import cl.koritsu.valued.domain.enums.TipoPersona;
import cl.koritsu.valued.services.ValuedService;


public class ClienteEditor extends VerticalLayout {


    public static final String ID = "clientewindow";
    protected BeanFieldGroup<Cliente> fieldGroup = new BeanFieldGroup<Cliente>(Cliente.class);
    
    /*
     * Fields for editing the User object are defined here as class members.
     * They are later bound to a FieldGroup by calling
     * fieldGroup.bindMemberFields(this). The Fields' values don't need to be
     * explicitly set, calling fieldGroup.setItemDataSource(user) synchronizes
     * the fields with the user object.
     */
    @PropertyId("rut")
    private TextField rutField;
    @PropertyId("nombres")
    private TextField nombresField;
    @PropertyId("apellidoPaterno")
    private TextField apellidoPaternoField;
    @PropertyId("apellidoMaterno")
    private TextField apellidoMaternoField;
    @PropertyId("razonSocial")
    private TextField razonSocialField;
    @PropertyId("direccion")
    private TextField direccionField;
    @PropertyId("telefonoFijo")
    private TextField telefonoFijoField;
    @PropertyId("factorKm")
    private TextField factorKmField;
    @PropertyId("tipoPersona")
    private OptionGroup tipoPersonaField;
    @PropertyId("multiRut")
    private OptionGroup multirutField;
    
    private HorizontalLayout tbMultirut, tbContactos;
    protected BeanItemContainer<Contacto> beanItemContacto = new BeanItemContainer<Contacto>(Contacto.class);
    protected BeanItemContainer<RazonSocial> beanItemRazon = new BeanItemContainer<RazonSocial>(RazonSocial.class);
    BeanFieldGroup<Contacto> fg;
    ValuedService service;
      
    public ClienteEditor(final Cliente cliente,ValuedService service) {
    	init(cliente,service);
    }
    
    ClienteEditor(ValuedService service) {
    	init(new Cliente(),service);
    }
    
    public void init(Cliente cliente,ValuedService service) {
        //addStyleName("profile-window");
    	this.service = service;
        setId(ID);
        Responsive.makeResponsive(this);

		setSizeFull();
		
		FormLayout detailLayout = new FormLayout();
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		
		Panel p = new Panel(detailLayout);
		p.setCaption("Crear Nuevo Cliente");
		p.setSizeFull();
		p.setScrollTop(100);
		addComponent(p);
		setExpandRatio(p, 1.0f);

		buildFormCliente(detailLayout);
              
        tbContactos = buildTableContact();
        detailLayout.addComponent(tbContactos);
        
        tbMultirut = buildTableMultiRut();
        detailLayout.addComponent(tbMultirut);
        tbMultirut.setVisible(false);
        
        addComponent(buildFooter());
        
        fieldGroup.bindMemberFields(this);
        cliente.setTipoPersona(TipoPersona.JURIDICA);
        fieldGroup.setItemDataSource(cliente);
        
        addListeners();
    }
    
    private void addListeners() {
        tipoPersonaField.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
            	tbContactos.setVisible(tipoPersonaField.isSelected(TipoPersona.JURIDICA));
            	multirutField.setVisible(tipoPersonaField.isSelected(TipoPersona.JURIDICA));
            	factorKmField.setVisible(tipoPersonaField.isSelected(TipoPersona.JURIDICA));
            	
            	//oculta los nombres
            	nombresField.setVisible(tipoPersonaField.isSelected(TipoPersona.NATURAL));
            	nombresField.setRequired(tipoPersonaField.isSelected(TipoPersona.NATURAL));
            	apellidoPaternoField.setVisible(tipoPersonaField.isSelected(TipoPersona.NATURAL));
            	apellidoMaternoField.setVisible(tipoPersonaField.isSelected(TipoPersona.NATURAL));
            	//muestra la razon social
            	razonSocialField.setVisible(tipoPersonaField.isSelected(TipoPersona.JURIDICA));
            	razonSocialField.setRequired(tipoPersonaField.isSelected(TipoPersona.JURIDICA));
            }
        });

        multirutField.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (multirutField.isSelected(1)) {
                	tbMultirut.setVisible(true);
                } else if (multirutField.isSelected(2)) {
                	tbMultirut.setVisible(false);
                }
            }
        });
	}

	private Component buildFormCliente(FormLayout details) {
//        HorizontalLayout root = new HorizontalLayout();
        
//        FormLayout details = new FormLayout();
//        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
//        root.addComponent(details);
//        root.setExpandRatio(details, 1);
               
        tipoPersonaField = new OptionGroup("Tipo Persona");
        
        for(TipoPersona tipoPersona : TipoPersona.values()) {
        	tipoPersonaField.addItem(tipoPersona);
        }
        tipoPersonaField.addStyleName("horizontal");
        tipoPersonaField.setRequired(true);

        details.addComponent(tipoPersonaField);
        tipoPersonaField.focus();        
        
        multirutField = new OptionGroup("¿MultiRut?");
        multirutField.addItem(1);
        multirutField.addItem(2);
        multirutField.setItemCaption(1, "Si");
        multirutField.setItemCaption(2, "No");
        
        multirutField.setConverter(new Converter() {

			@Override
			public Object convertToModel(Object value, Class targetType, Locale locale) throws ConversionException {
				if(value == null ) return null;
				
				Integer numero = (Integer)value;
				if(numero == 1)
					return true;
				else if(numero == 2 )
					return false;
				else
					throw new ConversionException("Error al transformar el valor "+value+" a boolean");
			}

			@Override
			public Object convertToPresentation(Object value, Class targetType, Locale locale)
					throws ConversionException {
				
				if(value == null ) return null;
				
				Boolean condicion = (Boolean)value;
				if(condicion)
					return 1;
				else
					return 2;
			}

			@Override
			public Class getModelType() {
				return Boolean.class;
			}

			@Override
			public Class getPresentationType() {
				return Integer.class;
			}
		});
        
        multirutField.setImmediate(true);
        multirutField.addStyleName("horizontal");

        details.addComponent(multirutField);
        multirutField.focus();    

        rutField = new TextField("RUT");
        rutField.setInputPrompt("11111111-1");
        rutField.setRequired(true);rutField.setRequired(true);rutField.setRequiredError("El campo Rut es requerido");
        details.addComponent(rutField);
        
        nombresField = new TextField("Nombre");
        nombresField.setVisible(false);nombresField.setRequiredError("El campo Nombre es requerido");
        details.addComponent(nombresField);
        
        apellidoPaternoField = new TextField("Apellido Paterno");
        apellidoPaternoField.setVisible(false);
        details.addComponent(apellidoPaternoField);
        
        apellidoMaternoField = new TextField("Apellido Materno");
        apellidoMaternoField.setVisible(false);
        details.addComponent(apellidoMaternoField);
        
        razonSocialField = new TextField("Razón Social");
        details.addComponent(razonSocialField);razonSocialField.setRequired(true);razonSocialField.setRequiredError("El campo Nombre es requerido");
        
        direccionField = new TextField("Dirección");
        details.addComponent(direccionField);
        
        telefonoFijoField = new TextField("Teléfono Fijo");
        details.addComponent(telefonoFijoField);
        
        factorKmField = new TextField("Factor Km/UF");
        details.addComponent(factorKmField);
       
        return details;
    }

    private HorizontalLayout buildTableContact() {
        HorizontalLayout root = new HorizontalLayout();
        root.setMargin(false);
        root.setSpacing(false);
        root.setWidth("100%");
        
        FormLayout details = new FormLayout();
        details.setMargin(false);
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);
    	
    	Label section = new Label("Lista de Contactos");
        section.addStyleName(ValoTheme.LABEL_H4);
        section.addStyleName(ValoTheme.LABEL_COLORED);
        details.addComponent(section);
        
		Button btnAddContacto = new Button(null,FontAwesome.PLUS);
		details.addComponent(btnAddContacto);
		details.setComponentAlignment(btnAddContacto, Alignment.TOP_RIGHT);

		btnAddContacto.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				Contacto contacto = new Contacto();
				beanItemContacto.addBean(contacto);
			}
		});	
		
		final Table tableContacto = new Table(null,beanItemContacto);
		tableContacto.setPageLength(3);
		tableContacto.setWidth("100%");
		tableContacto.setImmediate(true);
		tableContacto.setTableFieldFactory(new TableFieldFactory() {
			
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				Field<?> field = null; 
				if(propertyId.equals("apellidoMaterno") || propertyId.equals("nombre") || propertyId.equals("apellidoPaterno") || propertyId.equals("telefonoMovil")){
					field = new TextField();
					((TextField)field).setImmediate(true);
				} else if(  propertyId.equals("cargo") ){
						field = new ComboBox();
						((ComboBox)field).setItemCaptionMode(ItemCaptionMode.PROPERTY);
						((ComboBox)field).setItemCaptionPropertyId("nombre");
						BeanItemContainer<Cargo> ds = new BeanItemContainer<Cargo>(Cargo.class);
						List<Cargo> cargos = service.getCargos();
						ds.addAll(cargos);
						((ComboBox)field).setContainerDataSource(ds);
				} else if( propertyId.equals("eliminar")) {
					return null;
				}
				
				field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
					
				return field;
			}
		});				

		tableContacto.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el contacto seleccionado?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									beanItemContacto.removeItem(itemId);
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});
		
		tableContacto.setContainerDataSource(beanItemContacto);
		tableContacto.setVisibleColumns("nombre","apellidoPaterno","apellidoMaterno","cargo","telefonoMovil","eliminar");
		tableContacto.setColumnHeaders("Nombre","Apellido Paterno","Apellido Materno","Cargo","Telefono","Acciones");
		tableContacto.setEditable(true);				
		details.addComponent(tableContacto);
		//details.setComponentAlignment(root, Alignment.TOP_RIGHT);
	    
	    return root;
    }
    
    /*
     * 
     */
    private HorizontalLayout buildTableMultiRut() {
    	HorizontalLayout root = new HorizontalLayout();
    	root.setMargin(false);
        root.setSpacing(false);
    	root.setWidth("100%");
         
        FormLayout details = new FormLayout();
        details.setMargin(false);
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);
      	
      	Label section = new Label("Lista de Razones Sociales");
	    section.addStyleName(ValoTheme.LABEL_H4);
	    section.addStyleName(ValoTheme.LABEL_COLORED);
	    details.addComponent(section);
	     
		Button btnAddRazon = new Button(null,FontAwesome.PLUS);
		details.addComponent(btnAddRazon);
		details.setComponentAlignment(btnAddRazon, Alignment.TOP_RIGHT);

		btnAddRazon.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				RazonSocial razonSocial = new RazonSocial();
				beanItemRazon.addBean(razonSocial);
			}
		});	
		
		final Table tableRazonSocial = new Table(null,beanItemRazon);
		tableRazonSocial.setPageLength(3);
		tableRazonSocial.setWidth("100%");
		tableRazonSocial.setImmediate(true);
		tableRazonSocial.setTableFieldFactory(new TableFieldFactory() {
			
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				Field<?> field = null; 
				if(propertyId.equals("nombre") || propertyId.equals("rut")){
					field = new TextField();
					if(propertyId.equals("rut"))
						((TextField) field).setInputPrompt("11111111-1");
					((TextField)field).setImmediate(true);
				}else
					return null;
					
				return field;
			}
		});				

		tableRazonSocial.addGeneratedColumn("eliminar", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button(null,new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar la razon social seleccionada?",
								"Eliminar", "Cancelar", new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									beanItemRazon.removeItem(itemId);
								}
							}
						});
					}
				}){{setIcon(FontAwesome.TRASH_O);}};
			}
		});
		
		tableRazonSocial.setContainerDataSource(beanItemRazon);
		tableRazonSocial.setVisibleColumns("nombre","rut","eliminar");
		tableRazonSocial.setColumnHeaders("Razón Social","RUT","Acciones");
		tableRazonSocial.setEditable(true);				
		details.addComponent(tableRazonSocial);
 	    
 	    return root;

    }    
    
    Button btnCancelar = new Button("Cancelar");
    Button btnGuadar = new Button("Aceptar");
    
    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
//        footer.setWidth(100.0f, Unit.PERCENTAGE);

        btnGuadar.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnGuadar.setIcon(FontAwesome.SAVE);
        btnGuadar.focus();
		
		btnCancelar.addStyleName("link");
        
        footer.addComponent(btnGuadar);
        footer.addComponent(btnCancelar);
        footer.setComponentAlignment(btnGuadar, Alignment.TOP_LEFT);
        footer.setComponentAlignment(btnCancelar, Alignment.TOP_LEFT);
        return footer;
    }

	public Button getBtnCancelar() {
		return btnCancelar;
	}


	public Button getBtnGuadar() {
		return btnGuadar;
	}


    
}
