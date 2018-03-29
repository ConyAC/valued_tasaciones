package cl.koritsu.valued.view.facturacion;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import ru.xpoft.vaadin.VaadinView;
import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.nuevatasacion.ClienteEditor;
import cl.koritsu.valued.view.transactions.MisSolicitudesView;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = FacturacionView.NAME, cached = true)
public class FacturacionView extends VerticalLayout implements View {
	
Logger logger = LoggerFactory.getLogger(MisSolicitudesView.class);
	
	public static final String NAME = "facturar";
	
    ComboBox cbTasador,cbEstado,cbRegion,cbComuna, cbCliente, cbDireccion;
    String stNroTasacion;
    EstadoSolicitud stEstado;
    Usuario lTasador;
    Region lRegion;
    Comuna lComuna;
    Cliente lCliente;
    Bien stDireccion;
    TextField nroTasacion;
    Table table;

    @Autowired
    ValuedService service;

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@PostConstruct
    public void init(){
        setSizeFull();
        addStyleName("transactions");
//        ValuedEventBus.register(this);	
		
        addComponent(buildToolbar());
        
        addComponent(buildFiltro());

        table = buildTable();
        addComponent(table);
        setExpandRatio(table, 1);
    }

	
	private Component buildToolbar() {
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName("viewheader");
		header.setSpacing(true);
		Responsive.makeResponsive(header);

		Label title = new Label("Facturar");
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H1);
		title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		header.addComponent(title);

		return header;
	}
   
	private VerticalLayout buildFiltro() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);

		HorizontalLayout hl = new HorizontalLayout();
		vl.addComponent(hl);
		hl.setSpacing(true);

		cbCliente = new ComboBox("Cliente");
		hl.addComponent(cbCliente);
		cbCliente.setFilteringMode(FilteringMode.CONTAINS);
		cbCliente.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbCliente.setItemCaptionPropertyId("nombreCliente");
		BeanItemContainer<Cliente> cls = new BeanItemContainer<Cliente>(
				Cliente.class);
		List<Cliente> clientes = service.getClientes();
		cls.addAll(clientes);
		cbCliente.setContainerDataSource(cls);

		cbRegion = new ComboBox("Región");
		cbRegion.setImmediate(true);
		hl.addComponent(cbRegion);
		cbRegion.setContainerDataSource(new BeanItemContainer<Region>(
				Region.class));
		cbRegion.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbRegion.setItemCaptionPropertyId("nombre");

		// obtiene la lista de regiones
		List<Region> regiones = service.getRegiones();
		((BeanItemContainer<Region>) cbRegion.getContainerDataSource())
				.addAll(regiones);

		cbComuna = new ComboBox("Comuna");
		cbComuna.setImmediate(true);
		hl.addComponent(cbComuna);
		cbComuna.setContainerDataSource(new BeanItemContainer<Comuna>(
				Comuna.class));
		cbComuna.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbComuna.setItemCaptionPropertyId("nombre");

		cbDireccion = new ComboBox("Dirección");
		hl.addComponent(cbDireccion);
		cbDireccion.setFilteringMode(FilteringMode.CONTAINS);
		cbDireccion.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbDireccion.setItemCaptionPropertyId("direccion");
		BeanItemContainer<Bien> dir = new BeanItemContainer<Bien>(Bien.class);
		List<Bien> bienes = service.getDirecciones();
		dir.addAll(bienes);
		cbDireccion.setContainerDataSource(dir);

		cbRegion.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				Region region = (Region) event.getProperty().getValue();
				cbComuna.setEnabled(true);
				// obtiene la lista de regiones
				List<Comuna> comunas = service.getComunaPorRegion(region);
				cbComuna.removeAllItems();
				((BeanItemContainer<Comuna>) cbComuna.getContainerDataSource())
						.addAll(comunas);

			}
		});

		Button btnBuscar = new Button("Buscar", FontAwesome.SEARCH);
		hl.addComponent(btnBuscar);
		btnBuscar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				limpiarTabla();

//				lRegion = cbRegion.getValue() == null ? null
//						: ((Region) cbRegion.getValue());
//				lComuna = cbComuna.getValue() == null ? null
//						: ((Comuna) cbComuna.getValue());
//				stDireccion = cbDireccion.getValue() == null ? null
//						: ((Bien) cbDireccion.getValue());
//				lCliente = cbCliente.getValue() == null ? null
//						: ((Cliente) cbCliente.getValue());

				Notification.show("Próximamente",Type.WARNING_MESSAGE);

				if (table.getItemIds().isEmpty()) {
					Notification
							.show("No existen resultados que coincidan con los filtros aplicados...");
				}
			}

		});

		Button btnLimpiar = new Button("Limpiar", FontAwesome.RECYCLE);
		hl.addComponent(btnLimpiar);
		btnLimpiar.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 3844920778615955739L;

			@Override
			public void buttonClick(ClickEvent event) {
				limpiarTabla();
				cbRegion.select(null);
				cbComuna.select(null);
				cbCliente.select(null);
				cbDireccion.select(null);
			}
		});
		
		Button btnAgregar = new Button("Agregar", FontAwesome.FILE);
		hl.addComponent(btnAgregar);
		btnAgregar.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 3844920778615955739L;

			@Override
			public void buttonClick(ClickEvent event) {
				final EditarFactura editor = new EditarFactura(service);
			}
		});

		return vl;
	}
	
	 private Table buildTable() {
	    	Table table = new Table();
	    	table.setWidth("100%");

	    	table.addContainerProperty("Factura", String.class, null);
	    	table.addContainerProperty("N° Factura",  String.class, null);
	    	table.addContainerProperty("Cliente",  String.class, null);
	    	table.addContainerProperty("Acciones",  HorizontalLayout.class, null);
	    	
	    	HorizontalLayout hl = new HorizontalLayout();
	    	hl.setSpacing(true);
	    	Button btnEditar = new Button(null,FontAwesome.EDIT);
	    	btnEditar.setDescription("Editar");
	    	btnEditar.addClickListener(new Button.ClickListener() {
				
				public void buttonClick(ClickEvent event) {
					Notification.show("click editar");
				}
			});
	    	hl.addComponent(btnEditar);
	    	
	    	Button btnFacturar = new Button(null,FontAwesome.CHECK);
	    	btnFacturar.setDescription("Facturar");
	    	btnFacturar.addClickListener(new Button.ClickListener() {
				
				public void buttonClick(ClickEvent event) {
					facturar();
				}
			});
	    	hl.addComponent(btnFacturar);
	    	
	    	// Add a few other rows using shorthand addItem()
	    	table.addItem(new Object[]{"Factura Chile", "345554", "Banco de Chile", hl}, 1);

	    	// Show exactly the currently contained rows (items)
	    	table.setPageLength(table.size());
	    	
	    	return table;
	 }
	
	 public void limpiarTabla() {
	    	((BeanItemContainer<SolicitudTasacion>)table.getContainerDataSource()).removeAllItems();
	    }
	 
	 private Window facturar(){			 
		Window window = new Window(); 
	    window.setHeight("600px");
	    window.setWidth("450px");
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
	    
	    Label nombre = new Label();
	    nombre.setCaption("Nombre Factura");
	    nombre.setValue("Factura Chile");
	    fl.addComponent(nombre);
	    
	    Label numero = new Label();
	    numero.setCaption("N° Factura");
	    numero.setValue("333434");
	    fl.addComponent(numero);
	    
	    Label cliente = new Label();
	    cliente.setCaption("Cliente");
	    cliente.setValue("Banco de Chile");
	    fl.addComponent(cliente);
	    
	    Label fecha = new Label();
	    fecha.setCaption("Fecha");
	    fecha.setValue("23/02/2018");
	    fl.addComponent(fecha);
	    
	    Label monto = new Label();
	    monto.setCaption("Monto");
	    monto.setValue("$24.500.000");
	    fl.addComponent(monto);
	    
	    Label sectionPRograma = new Label("Infromación Tasaciones");
	    sectionPRograma.addStyleName(ValoTheme.LABEL_H3);
	    sectionPRograma.addStyleName(ValoTheme.LABEL_COLORED);	    
	    fl.addComponent(sectionPRograma);

	    Label t1 = new Label();
	    t1.setCaption("Tasación 1: ");
	    t1.setValue("BCH002, Av. Alameda 720");
	    fl.addComponent(t1);
	    
	    Label t2 = new Label();
	    t2.setCaption("Tasación 2: ");
	    t2.setValue("BCH0036, Av. Alameda 720");
	    fl.addComponent(t2);
	    
	    Label t3 = new Label();
	    t3.setCaption("Tasación 3: ");
	    t3.setValue(" BCH008, Av. Alameda 720");
	    fl.addComponent(t3);
	    
	    fl.addComponent(buildFooter());
	    
	    UI.getCurrent().addWindow(window);

		return window;		 
	 }
	 
	 private HorizontalLayout buildFooter() {
	    HorizontalLayout footer = new HorizontalLayout();
	    footer.setSpacing(true);
	    footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
	   // footer.setWidth(100.0f, Unit.PERCENTAGE);
	    
	    Button btnFacturar = new Button("Facturar");
	    btnFacturar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Próximamente",Type.WARNING_MESSAGE);
			}
		});
	    
	    btnFacturar.setIcon(FontAwesome.DOLLAR);
	    btnFacturar.addStyleName(ValoTheme.BUTTON_PRIMARY);
	    
	    Button btnCerrar = new Button("Cancelar");
	    btnCerrar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Próximamente",Type.WARNING_MESSAGE);
			}
		});
	    
	    btnCerrar.setIcon(FontAwesome.CLOSE);
	    btnCerrar.addStyleName(ValoTheme.BUTTON_PRIMARY);	
	    
	    footer.addComponent(btnFacturar);
	    footer.addComponent(btnCerrar);
	    footer.setComponentAlignment(btnCerrar, Alignment.BOTTOM_LEFT);
	    footer.setComponentAlignment(btnCerrar, Alignment.BOTTOM_RIGHT);
	    
	    return footer;
	}
}
