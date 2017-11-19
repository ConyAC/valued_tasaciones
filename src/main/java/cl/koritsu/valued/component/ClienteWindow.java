package cl.koritsu.valued.component;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.event.ValuedEvent.CloseOpenWindowsEvent;
import cl.koritsu.valued.event.ValuedEvent.ClienteUpdatedEvent;

@SuppressWarnings("serial")
public class ClienteWindow extends Window {

    public static final String ID = "clientewindow";
    private final BeanFieldGroup<Cliente> fieldGroup;
    
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
    @PropertyId("direccion")
    private TextField direccionField;
    @PropertyId("telefonoFijo")
    private TextField telefonoFijoField;
    @PropertyId("factorKm")
    private TextField factorKmField;
    @PropertyId("tipo")
    private OptionGroup tipoPersonaField;
    @PropertyId("multiRut")
    private OptionGroup multirutField;
    
    private HorizontalLayout tbMultirut, tbContactos;
      
    private ClienteWindow(final Cliente cliente) {
        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        setContent(content);
        
		FormLayout detailLayout = new FormLayout();
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		
		Panel p = new Panel(detailLayout);
		p.setCaption("Crear Nuevo Cliente");
		p.setSizeFull();
		content.addComponent(p);
		content.setExpandRatio(p, 1.0f);

		detailLayout.addComponent(buildFormCliente());
              
        tbContactos = buildTableContact();
        detailLayout.addComponent(tbContactos);
        tbContactos.setVisible(false);
        
        tbMultirut = buildTableMultiRut();
        detailLayout.addComponent(tbMultirut);
        tbMultirut.setVisible(false);
        
        content.addComponent(buildFooter());
        
        fieldGroup = new BeanFieldGroup<Cliente>(Cliente.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(cliente);
    }
    
    private Component buildFormCliente() {
        HorizontalLayout root = new HorizontalLayout();
        
        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);
               
        tipoPersonaField = new OptionGroup("Tipo Persona");
        tipoPersonaField.addItem(1);
        tipoPersonaField.addItem(2);
        tipoPersonaField.setItemCaption(1, "Persona Juridica");
        tipoPersonaField.setItemCaption(2, "Persona Natural");
        tipoPersonaField.setValue(1);
        tipoPersonaField.addStyleName("horizontal");
        tipoPersonaField.setRequired(true);

        tipoPersonaField.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (tipoPersonaField.isSelected(1)) {
                	tbContactos.setVisible(true);
                	multirutField.setVisible(true);
                } else if (tipoPersonaField.isSelected(2)) {
                	tbContactos.setVisible(false);
                	multirutField.setVisible(false);
                }
            }
        });
        details.addComponent(tipoPersonaField);
        tipoPersonaField.focus();        
        
        multirutField = new OptionGroup("¿MultiRut?");
        multirutField.addItem(1);
        multirutField.addItem(2);
        multirutField.setItemCaption(1, "Si");
        multirutField.setItemCaption(2, "No");
        multirutField.addStyleName("horizontal");
        multirutField.setImmediate(true);

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
        
        direccionField = new TextField("Dirección");
        details.addComponent(direccionField);
        
        telefonoFijoField = new TextField("Teléfono Fijo");
        details.addComponent(telefonoFijoField);
        
        factorKmField = new TextField("Factor Km/UF");
        details.addComponent(factorKmField);
       
        return root;
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
    
    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button btnGuadar = new Button("Aceptar");
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

                    ValuedEventBus.post(new ClienteUpdatedEvent());
                    close();
                } catch (CommitException e) {
                    Notification.show(e.getMessage(),
                            Type.ERROR_MESSAGE);
                }

            }
        });
        btnGuadar.focus();
        
		Button btnCancelar = new Button("Cancelar");
		btnCancelar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		btnCancelar.addStyleName("link");
        
        footer.addComponent(btnGuadar);
        footer.addComponent(btnCancelar);
        footer.setComponentAlignment(btnGuadar, Alignment.TOP_RIGHT);
        footer.setComponentAlignment(btnCancelar, Alignment.TOP_LEFT);
        return footer;
    }

    public static void open(final Cliente ciente) {
        ValuedEventBus.post(new CloseOpenWindowsEvent());
        Window w = new ClienteWindow(ciente);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
