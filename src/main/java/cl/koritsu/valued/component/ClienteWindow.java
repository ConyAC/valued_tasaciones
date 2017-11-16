package cl.koritsu.valued.component;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
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
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.ValuedUI;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.event.ValuedEvent.CloseOpenWindowsEvent;
import cl.koritsu.valued.event.ValuedEvent.ClienteUpdatedEvent;

@SuppressWarnings("serial")
public class ClienteWindow extends Window {

    public static final String ID = "clientewindow";

    private final BeanFieldGroup<Cliente> fieldGroup;
    
    private final Table table = new Table();
    private Button createReport;
    private static final DateFormat DATEFORMAT = new SimpleDateFormat(
            "MM/dd/yyyy hh:mm:ss a");
    private static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
    private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
            "theater", "room", "title", "seats" };
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
      
	public enum OpcionTipoPersona {
		PERSONA_JURIDICA, PERSONA_NATURAL;

		@Override
		public String toString() {
			switch (this) {
			case PERSONA_JURIDICA:
				return "Persona Juridica";
			case PERSONA_NATURAL:
				return "Persona Natural";
			}
			return super.toString();
		}
	}
	
    private ClienteWindow(final Cliente cliente,
            final boolean preferencesTabOpen) {
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

        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);

        detailsWrapper.addComponent(buildProfileTab());

        if (preferencesTabOpen) {
            detailsWrapper.setSelectedTab(1);
        }

        content.addComponent(buildFooter());

        fieldGroup = new BeanFieldGroup<Cliente>(Cliente.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(cliente);
    }

    private Table buildTable() {
        final Table table = new Table() {
            @Override
            protected String formatPropertyValue(final Object rowId,
                    final Object colId, final Property<?> property) {
                String result = super.formatPropertyValue(rowId, colId,
                        property);
                if (colId.equals("time")) {
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

        table.setColumnCollapsingAllowed(true);
        table.setColumnCollapsible("time", false);
        table.setColumnCollapsible("price", false);

        table.setColumnReorderingAllowed(true);
        
        table.setSortContainerPropertyId("time");
        table.setSortAscending(false);

        table.setColumnAlignment("seats", Align.RIGHT);
        table.setColumnAlignment("price", Align.RIGHT);

        table.setVisibleColumns("time", "country", "city", "theater", "room",
                "title", "seats", "price");
        table.setColumnHeaders("Time", "Country", "City", "Theater", "Room",
                "Title", "Seats", "Price");

        table.setFooterVisible(true);
        table.setColumnFooter("time", "Total");

        table.setColumnFooter(
                "price",
                "$"
                        + DECIMALFORMAT.format(ValuedUI.getDataProvider()
                                .getTotalSum()));

        // Allow dragging items to the reports menu
        table.setDragMode(TableDragMode.MULTIROW);
        table.setMultiSelect(true);

        

        table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                if (table.getValue() instanceof Set) {
                    Set<Object> val = (Set<Object>) table.getValue();
                    createReport.setEnabled(val.size() > 0);
                }
            }
        });
        table.setImmediate(true);

        return table;
    }
    
    private Component buildProfileTab() {
        HorizontalLayout root = new HorizontalLayout();
        
        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);
        
        tipoPersonaField = new OptionGroup("Tipo Persona", Arrays.asList(OpcionTipoPersona.values()));
        tipoPersonaField.setImmediate(true);
        tipoPersonaField.setRequired(true);
        tipoPersonaField.setValue(OpcionTipoPersona.PERSONA_JURIDICA);
        tipoPersonaField.addStyleName("horizontal");
        details.addComponent(tipoPersonaField);
		
        tipoPersonaField.addListener(new BlurListener() {
			@Override
			public void blur(BlurEvent event) {
				Notification.show("lalalal");
				buildTable();
				
			}
        });
        
        multirutField = new OptionGroup("¿MultiRut?");
        multirutField.addItem(Boolean.FALSE);
        multirutField.setItemCaption(Boolean.FALSE, "Si");
        multirutField.addItem(Boolean.TRUE);
        multirutField.setItemCaption(Boolean.TRUE, "No");
        multirutField.addStyleName("horizontal");
        details.addComponent(multirutField);

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
	    row1.getItemProperty("Nombre").setValue("Ramon Herrera");
	    row1.getItemProperty("Rut").setValue("10.564.343-k");
	    row1.getItemProperty("Cargo").setValue("Ejecutivo");
	    row1.getItemProperty("Telefono").setValue("22453434");
	    row1.getItemProperty("Acciones").setValue(new Button(FontAwesome.REMOVE));
	    table.addItem(new Object[]{"Pedro Soto", "9.234.322-2","Contador","2233443",new Button(FontAwesome.REMOVE)}, 2);
	    table.setPageLength(table.size());
	    details.addComponent(table);

        section = new Label("Lista de Razones Sociales");
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

        Button ok = new Button("Aceptar");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new ClickListener() {
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
        ok.focus();
        
		Button btnCancelar = new Button("Cancelar");
		btnCancelar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		btnCancelar.addStyleName("link");
        
        footer.addComponent(ok);
        footer.addComponent(btnCancelar);
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        footer.setComponentAlignment(btnCancelar, Alignment.TOP_LEFT);
        return footer;
    }

    public static void open(final Cliente ciente, final boolean preferencesTabActive) {
        ValuedEventBus.post(new CloseOpenWindowsEvent());
        Window w = new ClienteWindow(ciente, preferencesTabActive);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
    
    
}
