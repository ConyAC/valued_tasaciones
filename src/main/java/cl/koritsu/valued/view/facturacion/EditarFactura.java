package cl.koritsu.valued.view.facturacion;

import java.util.List;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.enums.EstadoFactura;
import cl.koritsu.valued.services.ValuedService;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class EditarFactura extends FormLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5035241314130096444L;
    public static final String ID = "editarfactura";	

    ValuedService service;
    FormLayout windows;
    
    ComboBox cbCliente, cbEstado;
	
	public EditarFactura(ValuedService service) {
    	init(service);
    }
	
	 public void init(ValuedService service) {
	        //addStyleName("profile-window");
	    	this.service = service;
	        setId(ID);
	        Responsive.makeResponsive(this);
			setSizeFull();
			
			buildResumenWindow();			
	 }
	 
	 private Window buildResumenWindow(){			
		 Window window = new Window();
		 window.setHeight("500px");
	     window.setModal(true);
		 window.setResizable(false);
		 window.center();
		 
		FormLayout fl = new FormLayout();
		window.setContent(fl);
		fl.setMargin(true);
		
		Label sectionInformacion = new Label("Información Factura");
      	sectionInformacion.addStyleName(ValoTheme.LABEL_H3);
      	sectionInformacion.addStyleName(ValoTheme.LABEL_COLORED);	    
      	fl.addComponent(sectionInformacion);
		
	    TextField nombre = new TextField();
	    nombre.setCaption("Nombre Factura");
	    fl.addComponent(nombre);
	    
	    TextField numero = new TextField();
	    numero.setCaption("N° Factura");
	    fl.addComponent(numero);
	    
		cbCliente = new ComboBox("Cliente");
		fl.addComponent(cbCliente);
		cbCliente.setFilteringMode(FilteringMode.CONTAINS);
		cbCliente.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbCliente.setItemCaptionPropertyId("nombreCliente");
		BeanItemContainer<Cliente> cls = new BeanItemContainer<Cliente>(
				Cliente.class);
		List<Cliente> clientes = service.getClientes();
		cls.addAll(clientes);
		cbCliente.setContainerDataSource(cls);
	    
	    DateField fecha = new DateField();
	    fecha.setCaption("Fecha");
	    fl.addComponent(fecha);
	    
        cbEstado = new ComboBox("Estado");
        cbEstado.setImmediate(true);
        fl.addComponent(cbEstado);
        for(EstadoFactura estado : EstadoFactura.values()) {
        	cbEstado.addItem(estado);
		}
        	    
	    TextField monto = new TextField();
	    monto.setCaption("Monto");
	    fl.addComponent(monto);
		
	    Label sectionTasaciones = new Label("Información Tasaciones");
	    sectionTasaciones.addStyleName(ValoTheme.LABEL_H3);
	    sectionTasaciones.addStyleName(ValoTheme.LABEL_COLORED);	    
      	fl.addComponent(sectionTasaciones);
      	
	    
	    Button btnAgregar = new Button("Agregar",FontAwesome.SEARCH);
	    btnAgregar.addClickListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 3844920778615955739L;

			public void buttonClick(ClickEvent event) {
				Notification.show("click Agregar");
			}
		});
		fl.addComponent(btnAgregar);
		
		Table tableTasaciones = new Table();
		tableTasaciones.addContainerProperty("N° Tasación", String.class, null);
		tableTasaciones.addContainerProperty("Fecha Encargo",  String.class, null);
		tableTasaciones.addContainerProperty("Dirección",  String.class, null);
		tableTasaciones.addContainerProperty("Tasador",  String.class, null);
		tableTasaciones.addContainerProperty("Acciones",  HorizontalLayout.class, null);    	
    	
    	HorizontalLayout hl2 = new HorizontalLayout();
    	hl2.setSpacing(true);
    	
    	Button btnEditar = new Button(null,FontAwesome.EDIT);
    	btnEditar.setDescription("Editar");
    	btnEditar.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				Notification.show("Próximamente",Type.WARNING_MESSAGE);
			}
		});
    	hl2.addComponent(btnEditar);
    	
    	Button btRemover = new Button(null,FontAwesome.CLOSE);
    	btRemover.setDescription("Remover");
    	btRemover.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				Notification.show("Próximamente",Type.WARNING_MESSAGE);
			}
		});
    	hl2.addComponent(btRemover);
    	
    	tableTasaciones.addItem(new Object[]{"BITAU9887", "23/02/2018", "Av. Alameda 720, Santiago, Región Metropolitana", "Navarrete, Francisca", hl2}, 1);
    	tableTasaciones.setPageLength(tableTasaciones.size());

		fl.addComponent(tableTasaciones);	
	    
		 fl.addComponent(buildFooter());
		 
		 UI.getCurrent().addWindow(window);	

		return window;
	}

	 private HorizontalLayout buildFooter() {
	    HorizontalLayout footer = new HorizontalLayout();
	    footer.setSpacing(true);
	    footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
	   // footer.setWidth(100.0f, Unit.PERCENTAGE);
	    
	    Button btnGuardar = new Button("Guardar");
	    btnGuardar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Próximamente",Type.WARNING_MESSAGE);
			}
		});
	    
	    btnGuardar.setIcon(FontAwesome.SAVE);
	    btnGuardar.addStyleName(ValoTheme.BUTTON_PRIMARY);	 
	    
	    Button btnCerrar = new Button("Cerrar");
	    btnCerrar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Próximamente",Type.WARNING_MESSAGE);
			}
		});
	    
	    btnCerrar.setIcon(FontAwesome.CLOSE);
	    btnCerrar.addStyleName(ValoTheme.BUTTON_PRIMARY);	    
	    
	    footer.addComponent(btnCerrar);
	    footer.setComponentAlignment(btnCerrar, Alignment.BOTTOM_RIGHT);
	    
	    return footer;
	}
}
