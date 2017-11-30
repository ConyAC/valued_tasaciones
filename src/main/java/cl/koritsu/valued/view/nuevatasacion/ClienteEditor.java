package cl.koritsu.valued.view.nuevatasacion;

import java.util.Locale;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.enums.TipoPersona;

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
      
    public ClienteEditor(final Cliente cliente) {
    	init(cliente);
    }
    
    ClienteEditor() {
    	init(new Cliente());
    }
    
    public void init(Cliente cliente) {
        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setSpacing(true);
		setMargin(true);
		setSizeFull();
        
		FormLayout detailLayout = new FormLayout();
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		
		Panel p = new Panel(detailLayout);
		p.setCaption("Crear Nuevo Cliente");
		p.setSizeFull();
		addComponent(p);
		setExpandRatio(p, 1.0f);

		detailLayout.addComponent(buildFormCliente());
              
        tbContactos = buildTableContact();
        detailLayout.addComponent(tbContactos);
        tbContactos.setVisible(false);
        
        tbMultirut = buildTableMultiRut();
        detailLayout.addComponent(tbMultirut);
        tbMultirut.setVisible(false);
        
        detailLayout.addComponent(buildFooter());
        
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
            	
            	//oculta los nombres
            	nombresField.setVisible(tipoPersonaField.isSelected(TipoPersona.NATURAL));
            	apellidoPaternoField.setVisible(tipoPersonaField.isSelected(TipoPersona.NATURAL));
            	apellidoMaternoField.setVisible(tipoPersonaField.isSelected(TipoPersona.NATURAL));
            	//muestra la razon social
            	razonSocialField.setVisible(tipoPersonaField.isSelected(TipoPersona.JURIDICA));
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

	private Component buildFormCliente() {
//        HorizontalLayout root = new HorizontalLayout();
        
        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
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
        multirutField.setVisible(false);

        rutField = new TextField("RUT");
        rutField.setRequired(true);
        details.addComponent(rutField);
        
        nombresField = new TextField("Nombre");
        details.addComponent(nombresField);
        
        apellidoPaternoField = new TextField("Apellido Paterno");
        details.addComponent(apellidoPaternoField);
        
        apellidoMaternoField = new TextField("Apellido Materno");
        details.addComponent(apellidoMaternoField);
        
        razonSocialField = new TextField("Razón Social");
        details.addComponent(razonSocialField);
        
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
        
        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);
    	
    	Label section = new Label("Lista de Contactos");
        section.addStyleName(ValoTheme.LABEL_H4);
        section.addStyleName(ValoTheme.LABEL_COLORED);
        details.addComponent(section);
        
        Table table = new Table();
        table.addContainerProperty("Nombre", String.class, null);
	    table.addContainerProperty("Rut",  String.class, null);
	    table.addContainerProperty("Cargo",  String.class, null);
	    table.addContainerProperty("Telefono",  String.class, null);
	    table.addContainerProperty("Acciones",  Button.class, null);
	    
	    Object newItemId = table.addItem();
	    Item row1 = table.getItem(newItemId);
	    table.addItem(new Object[]{"Ramon Herrera", "10.564.343-k","Ejecutivo","2233443",new Button(FontAwesome.REMOVE)}, 1);
	    table.addItem(new Object[]{"Pedro Soto", "9.234.322-2","Contador","2233443",new Button(FontAwesome.REMOVE)}, 2);
	    table.setPageLength(table.size());
	    details.addComponent(table);
	    
	    return root;
    }
    
    private HorizontalLayout buildTableMultiRut() {
    	HorizontalLayout root = new HorizontalLayout();
         
        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);
      	
      	Label section = new Label("Lista de Razones Sociales");
	    section.addStyleName(ValoTheme.LABEL_H4);
	    section.addStyleName(ValoTheme.LABEL_COLORED);
	    details.addComponent(section);
	     
	    Table table2 = new Table();
	    table2.addContainerProperty("Razón Social", String.class, null);
 	    table2.addContainerProperty("Rut",  String.class, null);
 	    table2.addContainerProperty("Dirección",  String.class, null);
 	    table2.addContainerProperty("Telefono",  String.class, null);
 	    table2.addContainerProperty("Acciones",  Button.class, null);
 	    
 	    table2.addItem(new Object[]{"Banco A", "90.234.322-2","Las Parcelas 343","2233443",new Button(FontAwesome.REMOVE)}, 1);
 	    table2.addItem(new Object[]{"Banco B", "90.234.322-2","Las Parcelas 343","2233443",new Button(FontAwesome.REMOVE)}, 2);
 	    table2.setPageLength(table2.size());
 	    details.addComponent(table2);
 	    
 	    return root;

    }    
    
    Button btnCancelar = new Button("Cancelar");
    Button btnGuadar = new Button("Aceptar");
    
    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        btnGuadar.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnGuadar.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    fieldGroup.commit();
                    // Updated user should also be persisted to database. But
                    // not in this demo.

                    Notification success = new Notification(
                            "Cliente almacenado correctamente");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());

                } catch (CommitException e) {
                    Notification.show(e.getMessage(),
                            Type.ERROR_MESSAGE);
                }

            }
        });
        btnGuadar.focus();
		
		btnCancelar.addStyleName("link");
        
        footer.addComponent(btnGuadar);
        footer.addComponent(btnCancelar);
        footer.setComponentAlignment(btnGuadar, Alignment.TOP_RIGHT);
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
