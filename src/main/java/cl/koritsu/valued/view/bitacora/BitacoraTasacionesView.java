package cl.koritsu.valued.view.bitacora;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import ru.xpoft.vaadin.VaadinView;
import cl.koritsu.valued.domain.Bitacora;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EtapaTasacion;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.transactions.MisSolicitudesView;
import cl.koritsu.valued.view.utils.Utils;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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
@VaadinView(value = BitacoraTasacionesView.NAME, cached = true)
public class BitacoraTasacionesView extends VerticalLayout implements View {

	Logger logger = LoggerFactory.getLogger(MisSolicitudesView.class);

	public static final String NAME = "bitacora_tasaciones";

	ComboBox cbUsuario,cbCliente,cbEtapaTasacion;
	TextField nroTasacion;
	DateField fechaInicio, fechaTermino;
	EtapaTasacion vEtapaTasacion;
	Usuario vUsuario;
	Cliente vCliente;
	String vNroTasacion;
	Date vfechaInicio, vfechaTermino;
	Table table;

	@Autowired
	ValuedService service;
	
	protected BeanItemContainer<Bitacora> bitacoraContainer = new BeanItemContainer<Bitacora>(Bitacora.class);

	@PostConstruct
	public void init() {
		setSizeFull();
		addStyleName("transactions");

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

		Label title = new Label("Bitácora Tasaciones");
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

		nroTasacion = new TextField("Número Tasación");
	    hl.addComponent(nroTasacion);		
		
		cbCliente = new ComboBox("Cliente");
		hl.addComponent(cbCliente);
		cbCliente.setFilteringMode(FilteringMode.CONTAINS);
		cbCliente.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbCliente.setItemCaptionPropertyId("nombreCliente");
		BeanItemContainer<Cliente> cls = new BeanItemContainer<Cliente>(Cliente.class);
		List<Cliente> clientes = service.getClientes();
		cls.addAll(clientes);
		cbCliente.setContainerDataSource(cls);

		cbUsuario = new ComboBox("Usuario/Tasador");
		cbUsuario.setImmediate(true);
        hl.addComponent(cbUsuario);
        cbUsuario.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        cbUsuario.setItemCaptionPropertyId("nombres");
		BeanItemContainer<Usuario> ds = new BeanItemContainer<Usuario>(Usuario.class);
		List<Usuario> usuarios = service.getTasadores();
		ds.addAll(usuarios);
		cbUsuario.setContainerDataSource(ds);
		
		cbEtapaTasacion = new ComboBox("Etapa Tasación");
		cbEtapaTasacion.setImmediate(true);
        hl.addComponent(cbEtapaTasacion);
        for(EtapaTasacion estado : EtapaTasacion.values()) {
        	cbEtapaTasacion.addItem(estado);
		}
        
        fechaInicio = new DateField("Fecha Inicio");
        fechaInicio.setDateFormat("dd/MM/yyyy");
        hl.addComponent(fechaInicio);
        
        fechaTermino = new DateField("Fecha Término");
        fechaTermino.setDateFormat("dd/MM/yyyy");
        hl.addComponent(fechaTermino);
        
		Button btnBuscar = new Button("Buscar", FontAwesome.SEARCH);
		hl.addComponent(btnBuscar);
		btnBuscar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				limpiarTabla();

				vUsuario = cbUsuario.getValue() == null ? null : ((Usuario) cbUsuario.getValue());
				vCliente = cbCliente.getValue() == null ? null : ((Cliente) cbCliente.getValue());
				vNroTasacion = nroTasacion.getValue() == null ? null : nroTasacion.getValue();
				vEtapaTasacion = cbEtapaTasacion.getValue() == null ? null : (EtapaTasacion) cbEtapaTasacion.getValue();
				vfechaInicio = fechaInicio.getValue() == null ? null : fechaInicio.getValue();
				vfechaTermino = fechaTermino.getValue() == null ? null : fechaTermino.getValue();

            	BuscarBitacoraSolicitudVO vo = new BuscarBitacoraSolicitudVO();
            	vo.setNroTasacion(vNroTasacion);
            	vo.setCliente(vCliente);
            	vo.setUsuario(vUsuario);
            	vo.setEtapaTasacion(vEtapaTasacion);
            	vo.setFechaInicio(vfechaInicio);
            	vo.setFechaTermino(vfechaTermino);
            	
            	List<Bitacora> bitacoras = service.getBitacoraFiltrada(vo);
            	bitacoraContainer.addAll(bitacoras); 

				if (table.getItemIds().isEmpty()) {
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
				cbUsuario.select(null);
				cbEtapaTasacion.select(null);
				cbCliente.select(null);
				nroTasacion.setValue("");
				fechaInicio.setValue(null);
				fechaTermino.setValue(null);
			}
		});

		Button btnDiagrama = new Button("Diagrama", FontAwesome.DIAMOND);
		hl.addComponent(btnDiagrama);
		btnDiagrama.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				Window window = new Window("Diagrama de Proceso");
                window.setWidth("80%");
                window.setHeight("50%");
                window.setResizable(false);
                window.setModal(true);
                window.setDraggable(false);
	            
				ThemeResource resource = new ThemeResource("img/flujoTasacion.png");
				Image image = new Image(null, resource);
				image.addStyleName("img-diagrama");
				window.setContent(image);
				
				UI.getCurrent().addWindow(window);
				
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
				if (colId.equals("fechaInicio") || colId.equals("fechaTermino")) {
					if (property.getValue() != null)
						result = Utils
								.formatoFecha(((Date) property.getValue()));
				}
				return result;
			}
		};

		table.setSizeFull();
		table.setSelectable(true);
		table.setColumnReorderingAllowed(true);
		table.setSortContainerPropertyId("fechaInicio");
		table.setContainerDataSource(new BeanItemContainer<Bitacora>(Bitacora.class));
		table.setSortAscending(false);
		
		bitacoraContainer.addNestedContainerProperty("solicitudTasacion.numeroTasacion");
		table.addGeneratedColumn("nombrecliente", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,Object columnId) {
				Bitacora b = ((BeanItem<Bitacora>) source.getItem(itemId)).getBean();
				return b.getSolicitudTasacion().getCliente() != null ? b.getSolicitudTasacion().getCliente().getNombreCliente() : "";
			}
		});
		
		table.addGeneratedColumn("nombreusuario", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,Object columnId) {
				Bitacora b = ((BeanItem<Bitacora>) source.getItem(itemId)).getBean();
				return b.getUsuario() != null ? b.getUsuario().getFullname() : "";
			}
		});

		table.addGeneratedColumn("etapa", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,Object columnId) {
				Bitacora b = ((BeanItem<Bitacora>) source.getItem(itemId)).getBean();
				return b.getEtapa() != null ? b.getEtapa().toString() : "";
			}
		});
		
		table.addGeneratedColumn("dias", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,Object columnId) {
				Bitacora b = ((BeanItem<Bitacora>) source.getItem(itemId)).getBean();
				DateTime inicio = new DateTime(b.getFechaInicio());
				DateTime termino = new DateTime(b.getFechaTermino());		    
				    
				return Days.daysBetween(inicio.toLocalDate(), termino.toLocalDate()).getDays();
			}
		});

		table.setContainerDataSource(bitacoraContainer);
		table.setVisibleColumns("solicitudTasacion.numeroTasacion", "nombrecliente","etapa","fechaInicio", "fechaTermino","dias","nombreusuario");
		table.setColumnHeaders("N° Tasación", "Cliente", "Etapa Tasación","Fecha Inicio Etapa", "Fecha Término Etapa","Duración (días)","Usuario Sistema");

		return table;
	}

	public void limpiarTabla() {
		bitacoraContainer.removeAllItems();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		table.removeAllItems();
		List<Bitacora> bitacoras = service.getBitacoras();
		bitacoraContainer.addAll(bitacoras);
	}
}
