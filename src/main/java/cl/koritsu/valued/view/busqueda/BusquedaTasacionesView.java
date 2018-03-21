package cl.koritsu.valued.view.busqueda;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

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
import cl.koritsu.valued.view.utils.ResumenTasacion;
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
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = BusquedaTasacionesView.NAME, cached = true)
public class BusquedaTasacionesView extends VerticalLayout implements View {
	
	public static final String NAME = "buscar_tasaciones";

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
    GoogleMap googleMap;

    public BusquedaTasacionesView() {
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

        Label title = new Label("Buscar Tasaciones");
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
				
        cbTasador = new ComboBox("Tasador");
        cbTasador.setImmediate(true);
        hl.addComponent(cbTasador);
        cbTasador.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbTasador.setItemCaptionPropertyId("nombres");
		BeanItemContainer<Usuario> ds = new BeanItemContainer<Usuario>(Usuario.class);
		cbTasador.setContainerDataSource(ds);
		
		List<Usuario> usuarios = service.getTasadores();
		ds.addAll(usuarios);		
        
        cbEstado = new ComboBox("Estado");
        cbEstado.setImmediate(true);
        hl.addComponent(cbEstado);
        for(EstadoSolicitud estado : EstadoSolicitud.values()) {
        	cbEstado.addItem(estado);
		}
        
        HorizontalLayout hl2 = new HorizontalLayout();
        vl.addComponent(hl2);
        hl2.setSpacing(true);
        
        cbRegion = new ComboBox("Región");
        cbRegion.setImmediate(true);
        hl2.addComponent(cbRegion);
    	cbRegion.setContainerDataSource(new BeanItemContainer<Region>(Region.class));
		cbRegion.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbRegion.setItemCaptionPropertyId("nombre");
		
		//obtiene la lista de regiones
		List<Region> regiones = service.getRegiones();
		((BeanItemContainer<Region>)cbRegion.getContainerDataSource()).addAll(regiones);
        
        cbComuna = new ComboBox("Comuna");
        cbComuna.setImmediate(true);
        hl2.addComponent(cbComuna);
        cbComuna.setContainerDataSource(new BeanItemContainer<Comuna>(Comuna.class));
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
				//obtiene la lista de regiones
				List<Comuna> comunas = service.getComunaPorRegion(region);
				cbComuna.removeAllItems();
				((BeanItemContainer<Comuna>)cbComuna.getContainerDataSource()).addAll(comunas);

			}
		});

        Button btnBuscar = new Button("Buscar",FontAwesome.SEARCH);
        hl.addComponent(btnBuscar);
        btnBuscar.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            	
            	limpiarTabla();
            	
            	stNroTasacion = nroTasacion.getValue() == null ? null : nroTasacion.getValue();
            	stEstado = cbEstado.getValue() == null ? null : (EstadoSolicitud) cbEstado.getValue();
            	lTasador = cbTasador.getValue() == null ? null : ((Usuario) cbTasador.getValue());
            	lRegion = cbRegion.getValue() == null ? null : ((Region) cbRegion.getValue());
            	lComuna = cbComuna.getValue() == null ? null : ((Comuna) cbComuna.getValue());
            	stDireccion = cbDireccion.getValue() == null ? null : ((Bien) cbDireccion.getValue());
            	lCliente = cbCliente.getValue() == null ? null : ((Cliente) cbCliente.getValue());
            	
            	BuscarSolicitudVO vo = new BuscarSolicitudVO();
            	vo.setNroTasacion(stNroTasacion);
            	vo.setEstado(stEstado);
            	vo.setTasador(lTasador);
            	vo.setRegion(lRegion);
            	vo.setComuna(lComuna);
            	vo.setCliente(lCliente);
            	vo.setDireccion(stDireccion);
            	
            	List<SolicitudTasacion> solicitudes = service.getTasacionesFiltradas(vo);
            	((BeanItemContainer<SolicitudTasacion>)table.getContainerDataSource()).addAll(solicitudes); 
            	
                if (table.getItemIds().isEmpty()){
                	Notification.show("No existen resultados que coincidan con los filtros aplciados...");
                }            	
            }
                
        });

        Button btnLimpiar = new Button("Limpiar",FontAwesome.RECYCLE);
        hl.addComponent(btnLimpiar);
        btnLimpiar.addClickListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 3844920778615955739L;

			@Override
			public void buttonClick(ClickEvent event) {				
				limpiarTabla();
				cbEstado.select(null);
				cbTasador.select(null);
				cbRegion.select(null);
				cbComuna.select(null);
				cbCliente.select(null);
				cbDireccion.select(null);
				nroTasacion.setValue("");	
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
                if (colId.equals("fechaEncargo") || colId.equals("fechaTasacion")) {
                	if (property.getValue() != null)
						result = Utils.formatoFecha(((Date) property.getValue()));
                } 
                return result;
            }
        };
        table.setSizeFull();
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setSelectable(true);

        table.setColumnReorderingAllowed(true);
        table.setContainerDataSource(new BeanItemContainer<SolicitudTasacion>(SolicitudTasacion.class));
        table.setSortAscending(false);
        
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
    	
    	table.addGeneratedColumn("estado",	new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId)).getBean();
				return sol.getEstado() != null ? sol.getEstado().toString(): "";
			}
		});
    	
        table.addGeneratedColumn("acciones", new ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				Button verTasacion = new Button(null, FontAwesome.LIST_ALT);
				verTasacion.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						BeanItem<SolicitudTasacion> sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId));
						final ResumenTasacion resumen = new ResumenTasacion(sol.getBean(), service, true, true);
					}
				});
				return verTasacion;
			}
		});
        
        table.setVisibleColumns("numeroTasacion", "nombrecliente", "estado", "fechaEncargo","fechaTasacion","direccion","tasador","acciones");
        table.setColumnHeaders("N° Tasación", "Cliente", "Estado","Fecha Encargo", "Fecha Visita","Dirección","Tasador","Ver");
        
        return table;
    }
	 
    public void limpiarTabla() {
    	((BeanItemContainer<SolicitudTasacion>)table.getContainerDataSource()).removeAllItems();
    }


    @Override
    public void enter(final ViewChangeEvent event) {
    	//limpia la tabla
    	table.removeAllItems();  
    }
}
