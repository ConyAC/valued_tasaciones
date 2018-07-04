package cl.koritsu.valued.view.facturacion;

import java.util.Date;
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
import cl.koritsu.valued.domain.Factura;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EstadoFactura;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.domain.enums.Permiso;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.transactions.MisSolicitudesView;
import cl.koritsu.valued.view.utils.SecurityHelper;
import cl.koritsu.valued.view.utils.Utils;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
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

	ComboBox cbTasador, cbRegion, cbComuna, cbCliente, cbDireccion, cbEstadoFactura;
	DateField fechaDesde, fechaHasta;
	Date vfechaDesde, vfechaHasta;
	EstadoFactura vEstadoFactura;
	String stNroFactura;
	EstadoSolicitud stEstado;
	Usuario lTasador;
	Region lRegion;
	Comuna lComuna;
	Cliente lCliente;
	Bien stDireccion;
	TextField nroFactura;
	Table tableFacturas;
	Button btnFacturar;
	
	protected BeanItemContainer<Factura> facturaContainer = new BeanItemContainer<Factura>(Factura.class);

	@Autowired
	ValuedService service;

	@PostConstruct
	public void init() {
		setSizeFull();
		addStyleName("transactions");

		addComponent(buildToolbar());

		addComponent(buildFiltro());

		tableFacturas = buildTable();
		addComponent(tableFacturas);
		setExpandRatio(tableFacturas, 1);
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

	/*
	 * Permite la creación del filtro para la busqueda.
	 */
	private VerticalLayout buildFiltro() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);

		HorizontalLayout hl = new HorizontalLayout();
		vl.addComponent(hl);
		hl.setSpacing(true);
		
		nroFactura = new TextField("N° Factura");
	    hl.addComponent(nroFactura);
	    
	    cbEstadoFactura = new ComboBox("Estado Factura");
	    cbEstadoFactura.setImmediate(true);
        hl.addComponent(cbEstadoFactura);
        for(EstadoFactura estado : EstadoFactura.values()) {
        	cbEstadoFactura.addItem(estado);
		}
        
        fechaDesde = new DateField("Fecha Desde");
        fechaDesde.setDateFormat("dd/MM/yyyy");
        hl.addComponent(fechaDesde);
        
        fechaHasta = new DateField("Fecha Hasta");
        fechaHasta.setDateFormat("dd/MM/yyyy");
        hl.addComponent(fechaHasta);
        
        HorizontalLayout hl2 = new HorizontalLayout();
		vl.addComponent(hl2);
		hl2.setSpacing(true);
        
		cbCliente = new ComboBox("Cliente");
		hl2.addComponent(cbCliente);
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
		hl2.addComponent(cbRegion);
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
		hl2.addComponent(cbComuna);
		cbComuna.setContainerDataSource(new BeanItemContainer<Comuna>(
				Comuna.class));
		cbComuna.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbComuna.setItemCaptionPropertyId("nombre");

		cbDireccion = new ComboBox("Dirección");
		hl2.addComponent(cbDireccion);
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

				lRegion = cbRegion.getValue() == null ? null : ((Region) cbRegion.getValue());
				lComuna = cbComuna.getValue() == null ? null : ((Comuna) cbComuna.getValue());
				stDireccion = cbDireccion.getValue() == null ? null : ((Bien) cbDireccion.getValue());
				lCliente = cbCliente.getValue() == null ? null : ((Cliente) cbCliente.getValue());
				stNroFactura = nroFactura.getValue() == null ? null : nroFactura.getValue();
				vEstadoFactura = cbEstadoFactura.getValue() == null ? null : (EstadoFactura) cbEstadoFactura.getValue();
				vfechaDesde = fechaDesde.getValue() == null ? null : fechaDesde.getValue();
				vfechaHasta = fechaHasta.getValue() == null ? null : fechaHasta.getValue();

            	BuscarFacturaVO vo = new BuscarFacturaVO();
            	vo.setNroFactura(stNroFactura);
            	vo.setRegion(lRegion);
            	vo.setComuna(lComuna);
            	vo.setCliente(lCliente);
            	vo.setDireccion(stDireccion);
            	vo.setEstadoFactura(vEstadoFactura);
            	vo.setFechaDesde(vfechaDesde);
            	vo.setFechaHasta(vfechaHasta);
            	
            	List<Factura> facturas = service.getFacturasFiltradas(vo);
            	((BeanItemContainer<Factura>)tableFacturas.getContainerDataSource()).addAll(facturas); 

				if (tableFacturas.getItemIds().isEmpty()) {
					Notification
							.show("No existen resultados que coincidan con los filtros aplicados...");
				}
			}

		});

		Button btnLimpiar = new Button("Limpiar", FontAwesome.RECYCLE);
		hl.addComponent(btnLimpiar);
		btnLimpiar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				limpiarTabla();
				cbRegion.select(null);
				cbComuna.select(null);
				cbCliente.select(null);
				cbDireccion.select(null);
				nroFactura.setValue("");
				fechaDesde.setValue(null);
				fechaHasta.setValue(null);
				cbEstadoFactura.setValue(null);
			}
		});

		Button btnAgregar = new Button("Agregar", FontAwesome.FILE);
		hl.addComponent(btnAgregar);
		hl.setComponentAlignment(btnAgregar, Alignment.BOTTOM_RIGHT);
		btnAgregar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().getNavigator().navigateTo(AdminFacturacionView.NAME+"/new");
			}
		});

		return vl;
	}

	private Table buildTable() {
		final Table table = new Table() {
			@Override
			protected String formatPropertyValue(final Object rowId,
					final Object colId, final Property<?> property) {
				String result = super.formatPropertyValue(rowId, colId,
						property);
				if (colId.equals("fecha")) {
					if (property.getValue() != null)
						result = Utils
								.formatoFecha(((Date) property.getValue()));
				} else if (colId.equals("montoManual")) {
					if (property != null && property.getValue() != null) {
						return "$"
								+ Utils.getDecimalFormatSinDecimal().format(
										property.getValue());
					} else {
						return "";
					}
				}
				return result;
			}
		};

		table.setSizeFull();
		table.setSelectable(true);

		table.setColumnReorderingAllowed(true);
		table.setContainerDataSource(new BeanItemContainer<Factura>(
				Factura.class));
		table.setSortAscending(false);

		table.addGeneratedColumn("nombrecliente", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Factura f = ((BeanItem<Factura>) source.getItem(itemId))
						.getBean();
				return f.getCliente() != null ? f.getCliente()
						.getNombreCliente() : "";
			}
		});

		table.addGeneratedColumn("estado", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Factura f = ((BeanItem<Factura>) source.getItem(itemId))
						.getBean();
				return f.getEstado() != null ? f.getEstado().toString() : "";
			}
		});

		table.addGeneratedColumn("acciones", new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId,
					final Object columnId) {

				HorizontalLayout hl = new HorizontalLayout();
				hl.setSpacing(true);
				Button btnEditar = new Button(null, FontAwesome.LIST_ALT);
				btnEditar.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						BeanItem<Factura> f = ((BeanItem<Factura>) source
								.getItem(itemId));
						UI.getCurrent().getNavigator().navigateTo(AdminFacturacionView.NAME+"/"+f.getBean().getId());
					}
				});
				hl.addComponent(btnEditar);

				if (SecurityHelper.hasPermission(Permiso.FACTURAR)) {
					btnFacturar = new Button(null, FontAwesome.CHECK);
					btnFacturar.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							BeanItem<Factura> f = ((BeanItem<Factura>) source
									.getItem(itemId));
							buildFacturar(f.getBean());
						}
					});

					hl.addComponent(btnFacturar);
				}

				return hl;
			}
		});

		table.setContainerDataSource(facturaContainer);
		table.setVisibleColumns("estado", "numero", "nombrecliente", "fecha","montoManual", "acciones");
		table.setColumnHeaders("Estado", "N° Factura", "Cliente", "Fecha","Monto Factura", "Acciones");

		return table;
	}

	public void limpiarTabla() {
		facturaContainer.removeAllItems();
	}

	/* Funcion que permite marcar como pagada o anulada una factura */
	private Window buildFacturar(Factura f) {
		Window window = new Window("N° Factura: "+f.getNumero());
		window.setHeight("65%");
		window.setWidth("30%");
		window.setModal(true);
		window.setResizable(false);
		window.center();

		FormLayout fl = new FormLayout();
		window.setContent(fl);
		fl.setMargin(true);
		fl.setSpacing(true);

		Label sectionInformacion = new Label("Información Factura");
		sectionInformacion.addStyleName(ValoTheme.LABEL_H3);
		sectionInformacion.addStyleName(ValoTheme.LABEL_COLORED);
		fl.addComponent(sectionInformacion);

		Label nombre = new Label();
		nombre.setCaption("Nombre Factura");
		nombre.setValue((f.getNombre() != null) ? f.getNombre()
				: "No registrado");
		fl.addComponent(nombre);

		Label numero = new Label();
		numero.setCaption("N° Factura");
		numero.setValue(f.getNumero());
		fl.addComponent(numero);

		Label cliente = new Label();
		cliente.setCaption("Cliente");
		cliente.setValue(f.getCliente().getNombreCliente());
		fl.addComponent(cliente);

		Label fecha = new Label();
		fecha.setCaption("Fecha");
		fecha.setValue(Utils.formatoFecha(f.getFecha()));
		fl.addComponent(fecha);

		Label montoManual = new Label();
		montoManual.setCaption("Monto $");
		montoManual.setValue(Utils.getDecimalFormatSinDecimal().format(
				f.getMontoManual()));
		fl.addComponent(montoManual);
	
		if(!f.getSolicitudes().isEmpty()){
			Label sectionPRograma = new Label("Información Tasaciones");
			sectionPRograma.addStyleName(ValoTheme.LABEL_H3);
			sectionPRograma.addStyleName(ValoTheme.LABEL_COLORED);
			fl.addComponent(sectionPRograma);

			for(SolicitudTasacion sol: f.getSolicitudes()){
				Label tasacion = new Label();
				tasacion.setCaption("Tasación :");
				tasacion.setValue(sol.getNumeroTasacion()+", "+sol.getCliente().getNombreCliente()+", "+sol.getBien().getDireccion()+", "+Utils.formatoFecha(sol.getFechaEncargo()));
				fl.addComponent(tasacion);
			}
			
		}else{
			Label tasacion = new Label();
			tasacion.setCaption("No existen tasaciones relacionadas a la factura.");
			fl.addComponent(tasacion);
			btnFacturar.setEnabled(false);
		}

		fl.addComponent(buildFooter(window, f));

		UI.getCurrent().addWindow(window);

		return window;
	}

	private HorizontalLayout buildFooter(final Window w,final Factura f) {
		HorizontalLayout footer = new HorizontalLayout();
		footer.setSpacing(true);
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

		btnFacturar = new Button("Facturar");
		btnFacturar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try{
					f.setEstado(EstadoFactura.PAGADA);
					service.saveFactura(f);
					Notification.show("Cambio de estado de la factura a Pagada.", Type.ASSISTIVE_NOTIFICATION);					

					((UI) w.getParent()).removeWindow(w);
				}catch(Exception e){
					Notification.show("Se ha producido un error al realizar la facturación.", Type.ERROR_MESSAGE);
				}
			}
		});

		btnFacturar.setIcon(FontAwesome.DOLLAR);
		btnFacturar.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnFacturar.focus();

		Button btnAnular = new Button("Anular");
		btnAnular.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try{
					f.setEstado(EstadoFactura.ANULADA);
					service.saveFacturaAnulada(f);
					Notification.show("Cambio de estado de la factura a Anulada, las tasaciones han sido liberadas.", Type.ASSISTIVE_NOTIFICATION);					

					((UI) w.getParent()).removeWindow(w);
				}catch(Exception e){
					System.out.println(e);
					Notification.show("Se ha producido un error al realizar la anulación.", Type.ERROR_MESSAGE);
				}
			}
		});

		btnAnular.setIcon(FontAwesome.REMOVE);
		btnAnular.addStyleName(ValoTheme.BUTTON_PRIMARY);

		Button btnCerrar = new Button("Cancelar");
		btnCerrar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				((UI) w.getParent()).removeWindow(w);
			}
		});

		btnCerrar.setIcon(FontAwesome.CLOSE);
		btnCerrar.addStyleName(ValoTheme.BUTTON_PRIMARY);

		footer.addComponent(btnFacturar);
		//footer.addComponent(btnCerrar);
		footer.addComponent(btnAnular);
		footer.setComponentAlignment(btnFacturar, Alignment.BOTTOM_LEFT);
		footer.setComponentAlignment(btnAnular, Alignment.BOTTOM_CENTER);
		//footer.setComponentAlignment(btnCerrar, Alignment.BOTTOM_RIGHT);

		return footer;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// limpia la tabla
		tableFacturas.removeAllItems();
		List<Factura> facturas = service.getFacturas();
		facturaContainer.addAll(facturas);
	}
}
