package cl.koritsu.valued.view.facturacion;

import java.util.Date;
import java.util.List;

import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Factura;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.busqueda.BuscarSolicitudVO;
import cl.koritsu.valued.view.utils.ResumenTasacion;
import cl.koritsu.valued.view.utils.Utils;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class EditarTasaciones extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3181086229335420526L;

	public static final String ID = "editartasaciones";
	
	protected BeanFieldGroup<Factura> fieldGroup = new BeanFieldGroup<Factura>(Factura.class);
	protected BeanItemContainer<Factura> facturaContainer = new BeanItemContainer<Factura>(Factura.class);
	protected BeanItemContainer<SolicitudTasacion> solicitudContainer = new BeanItemContainer<SolicitudTasacion>(SolicitudTasacion.class);
	
	ComboBox cbCliente, cbRegion, cbComuna, cbDireccion;
	TextField nroTasacion;
	String stNroTasacion;
	ValuedService service;
	List<Factura> facturas;
	Table tableTasaciones;
	Region lRegion;
	Comuna lComuna;
	Cliente lCliente;
	Bien stDireccion;
	Window win;

	public EditarTasaciones(final Factura factura, ValuedService service,BeanFieldGroup<Factura> fieldGroup) {
		init(factura, service,fieldGroup, null);
	}

	EditarTasaciones(ValuedService service,BeanFieldGroup<Factura> fieldGroup,BeanItemContainer<SolicitudTasacion> solicitudContainer) {
		init(new Factura(), service, fieldGroup,solicitudContainer);
	}

	public void init(Factura factura, ValuedService service,BeanFieldGroup<Factura> fieldGroup,BeanItemContainer<SolicitudTasacion> solicitudContainer) {
		this.service = service;
		this.fieldGroup = fieldGroup;
		this.solicitudContainer = solicitudContainer;
		setId(ID);
		Responsive.makeResponsive(this);
		
		setSizeFull();
		setModal(true);
		setContent(buildTasaciones());
		center();

		win = this;
		
		fieldGroup.bindMemberFields(this);
		
		if(fieldGroup.getField("cliente").getValue() != null){
			cbCliente.select(fieldGroup.getField("cliente"));
			BuscarSolicitudVO vo = new BuscarSolicitudVO();
	    	vo.setCliente((Cliente)fieldGroup.getField("cliente").getValue());
	    	vo.setNroTasacion(null);
	    	
	    	List<SolicitudTasacion> solicitudes = service.getTasacionesFiltradas(vo);
	    	((BeanItemContainer<SolicitudTasacion>)tableTasaciones.getContainerDataSource()).addAll(solicitudes); 
		}
		
		UI.getCurrent().addWindow(this);
	}

	Button btnCancelar = new Button("Cancelar");
	Button btnGuardar = new Button("Aceptar");

	private VerticalLayout buildTasaciones() {		
		VerticalLayout root = new VerticalLayout();	
		root.setSpacing(true);
		root.setMargin(true);
		
		HorizontalLayout hl = new HorizontalLayout();
		root.addComponent(hl);
		hl.setSpacing(true);
		
		nroTasacion = new TextField("Número Tasación");
	    hl.addComponent(nroTasacion);

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

				stNroTasacion = nroTasacion.getValue() == null ? null
						: nroTasacion.getValue();
				lRegion = cbRegion.getValue() == null ? null
						: ((Region) cbRegion.getValue());
				lComuna = cbComuna.getValue() == null ? null
						: ((Comuna) cbComuna.getValue());
				stDireccion = cbDireccion.getValue() == null ? null
						: ((Bien) cbDireccion.getValue());
				lCliente = cbCliente.getValue() == null ? null
						: ((Cliente) cbCliente.getValue());

				BuscarSolicitudVO vo = new BuscarSolicitudVO();
            	vo.setNroTasacion(stNroTasacion);
            	vo.setRegion(lRegion);
            	vo.setComuna(lComuna);
            	vo.setCliente(lCliente);
            	vo.setDireccion(stDireccion);
            	
            	List<SolicitudTasacion> solicitudes = service.getTasacionesFiltradas(vo);
            	((BeanItemContainer<SolicitudTasacion>)tableTasaciones.getContainerDataSource()).addAll(solicitudes); 

				if (tableTasaciones.getItemIds().isEmpty()) {
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
			}
		});
		
		tableTasaciones = buildTableTasaciones();
		root.addComponent(tableTasaciones);
		
		root.addComponent(buildFooter());

		return root;
	}
	
	private Table buildTableTasaciones(){
		final Table table = new Table() {
			@Override
			protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
				String result = super.formatPropertyValue(rowId, colId,property);
				if (colId.equals("fechaEncargo") || colId.equals("fechaTasacion")) {
					if (property.getValue() != null)
						result = Utils.formatoFecha(((Date) property.getValue()));
				}
				return result;
			}
		};
		
		table.setImmediate(true);
		table.setContainerDataSource(new BeanItemContainer<SolicitudTasacion>(SolicitudTasacion.class));
		table.setSizeFull();
		table.setSelectable(true);
		table.setColumnReorderingAllowed(true);
		table.setSortAscending(false);		
        
		table.addGeneratedColumn("acciones", new ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, final Object itemId,Object columnId) {
				HorizontalLayout hl = new HorizontalLayout();
				
				Button verTasacion = new Button(null, FontAwesome.FILE);
				verTasacion.setDescription("Resumen");
				verTasacion.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						BeanItem<SolicitudTasacion> sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId));
						final ResumenTasacion resumen = new ResumenTasacion(sol.getBean(), service, true, true);
					}
				});				
				hl.addComponent(verTasacion);			
				hl.setSpacing(true);				
				
				CheckBox agregarTasacion = new CheckBox();
				agregarTasacion.setDescription("Seleccionar");
				agregarTasacion.addValueChangeListener(new Property.ValueChangeListener() {
				    public void valueChange(ValueChangeEvent event) {
				    	BeanItem<SolicitudTasacion> sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId));
				    	fieldGroup.getItemDataSource().getBean().getSolicitudes().add(sol.getBean());
				    }
				});
				agregarTasacion.setImmediate(true);
				hl.addComponent(agregarTasacion);
				
				return hl;
			}
		});		
        
        table.addGeneratedColumn("nombrecliente", new ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId)).getBean();
				return sol.getCliente() != null ? sol.getCliente().getNombreCliente() : "";
			}
		});        
		
        table.addGeneratedColumn("direccion", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId)).getBean();
				Bien bien = sol.getBien();
				return (bien != null && bien.getDireccion() != null) ? bien
						.getDireccion()
						+ " "
						+ bien.getNumeroManzana()
						+ " "
						+ bien.getComuna().getNombre()
						+ " "
						+ bien.getComuna().getRegion().getNombre()
						: "";
			}
		});
		
    	table.addGeneratedColumn("tasador",	new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId)).getBean();
				return sol.getTasador() != null ? sol.getTasador().getFullname() : "";
			}
		});
    	
		table.setVisibleColumns("numeroTasacion","nombrecliente","fechaEncargo", "direccion","acciones");
		table.setColumnHeaders("N° Tasación","Cliente","Fecha Encargo","Dirección","Acciones");
		
		return table;
	}
	
	/*
	 * Permite la creación de los botones que almacenan o cancelan la modificación.
	 */
	private HorizontalLayout buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.setSpacing(true);
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

		btnGuardar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
		    	solicitudContainer.addAll(fieldGroup.getItemDataSource().getBean().getSolicitudes());
		    	((UI) win.getParent()).removeWindow(win);
			}
		});
		btnGuardar.setIcon(FontAwesome.SAVE);
		btnGuardar.addStyleName(ValoTheme.BUTTON_PRIMARY);

		btnCancelar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				solicitudContainer.removeAllItems();
				fieldGroup.discard();
				((UI) win.getParent()).removeWindow(win);
			}
		});

		btnCancelar.addStyleName("link");

		footer.addComponent(btnGuardar);
		footer.addComponent(btnCancelar);
		footer.setComponentAlignment(btnGuardar, Alignment.TOP_RIGHT);
		footer.setComponentAlignment(btnCancelar, Alignment.TOP_RIGHT);

		return footer;
	}

	public Button getBtnCancelar() {
		return btnCancelar;
	}

	public Button getBtnGuadar() {
		return btnGuardar;
	}
	
	public void limpiarTabla() {
		((BeanItemContainer<Factura>) tableTasaciones.getContainerDataSource()).removeAllItems();
	}
}

