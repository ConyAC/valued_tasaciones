package cl.koritsu.valued.view.busqueda;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import ru.xpoft.vaadin.VaadinView;
import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.ObraComplementaria;
import cl.koritsu.valued.domain.ProgramaBien;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.domain.enums.EstadoTasacion;
import cl.koritsu.valued.services.ValuedService;
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
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
@VaadinView(value = BusquedaTasacionesView.NAME, cached = true)
public class BusquedaTasacionesView extends VerticalLayout implements View {
	
	public static final String NAME = "buscar_tasaciones";

    ComboBox cbTasador,cbEstado,cbRegion,cbComuna;
    String stNroTasacion;
    EstadoSolicitud stEstado;
    Usuario lTasador;
    Region lRegion;
    Comuna lComuna;
   // Long lTasador, lRegion, lComuna;
    TextField nroTasacion;
    Table table;
    Window window;
    
    @Autowired
    ValuedService service;
    private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
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
        
        cbRegion = new ComboBox("Región");
        cbRegion.setImmediate(true);
        hl.addComponent(cbRegion);
    	cbRegion.setContainerDataSource(new BeanItemContainer<Region>(Region.class));
		cbRegion.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbRegion.setItemCaptionPropertyId("nombre");
		
		//obtiene la lista de regiones
		List<Region> regiones = service.getRegiones();
		((BeanItemContainer<Region>)cbRegion.getContainerDataSource()).addAll(regiones);
        
        cbComuna = new ComboBox("Comuna");
        cbComuna.setImmediate(true);
        hl.addComponent(cbComuna);
        cbComuna.setContainerDataSource(new BeanItemContainer<Comuna>(Comuna.class));
		cbComuna.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbComuna.setItemCaptionPropertyId("nombre");
        
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
            	
            	BuscarSolicitudVO vo = new BuscarSolicitudVO();
            	vo.setNroTasacion(stNroTasacion);
            	vo.setEstado(stEstado);
            	vo.setTasador(lTasador);
            	vo.setRegion(lRegion);
            	vo.setComuna(lComuna);
            	
            	Page<SolicitudTasacion> solicitudes = service.getBuscarTasaciones(new PageRequest(0, 200),vo);    	
            	((BeanItemContainer<SolicitudTasacion>)table.getContainerDataSource()).addAll(solicitudes.getContent()); 
            	
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
						buildResumen(sol.getBean());
					}
				});
				return verTasacion;
			}
		});
        
        table.setVisibleColumns("numeroTasacion", "nombrecliente", "estado", "fechaEncargo","fechaTasacion","direccion","tasador","acciones");
        table.setColumnHeaders("N° Tasación", "Cliente", "Estado","Fecha Encargo", "Fecha Visita","Dirección","Tasador","Ver");

        return table;
    }
    
	private Window buildResumen(SolicitudTasacion sol) {
		
		window = new Window(sol.getNumeroTasacion());
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		window.setHeight("500px");
		
		window.setModal(true);
		window.setResizable(false);
		window.center();
	 	
		FormLayout fl = new FormLayout();
		vl.addComponent(fl);
		fl.setMargin(true);
		//fl.addStyleName("outlined");
        
      	Label sectionCliente = new Label("Resumen");
      	sectionCliente.addStyleName(ValoTheme.LABEL_H3);
      	sectionCliente.addStyleName(ValoTheme.LABEL_COLORED);	    
	    fl.addComponent(sectionCliente);
	    
	    Label encargo = new Label();
	    encargo.setCaption("Fecha Encargo");
	    encargo.setValue(Utils.formatoFecha(sol.getFechaEncargo()));
	    fl.addComponent(encargo);
	    
	    Label cliente = new Label();
	    cliente.setCaption("Nombre Cliente");
	    cliente.setValue(sol.getCliente().getFullname());
	    fl.addComponent(cliente);
	    
	    Label estado = new Label();
	    estado.setCaption("Estado");
	    estado.setValue(sol.getEstado().name());
	    fl.addComponent(estado);
	    
	    Label informe = new Label();
	    informe.setCaption("Tipo Informe");
	    informe.setValue(sol.getTipoInformeString());
	    fl.addComponent(informe);
	    
	    Label clase = new Label();
	    clase.setCaption("Clase Bien");
	    clase.setValue(sol.getBien().getClase().toString());
	    fl.addComponent(clase);
	    
	    Label direccion = new Label();
	    direccion.setCaption("Dirección");
	    direccion.setValue(sol.getDireccionCompleta());
	    fl.addComponent(direccion);
	    
	 	Label sectionTasacion = new Label("Información Tasación");
	 	sectionTasacion.addStyleName(ValoTheme.LABEL_H3);
	 	sectionTasacion.addStyleName(ValoTheme.LABEL_COLORED);	    
	    fl.addComponent(sectionTasacion);
	    
	    Label tasador = new Label();
	    tasador.setCaption("Tasador");
	    tasador.setValue(sol.getTasador().getFullname());
	    fl.addComponent(tasador);
	    
	    if(sol.getFechaTasacion() != null){
		    Label visita = new Label();
		    visita.setCaption("Fecha Visita");
		    visita.setValue(Utils.formatoFecha(sol.getFechaTasacion()));
		    fl.addComponent(visita);
	    }
	    
	    if(sol.getFechaVisado() != null){
		    Label visado = new Label();
		    visado.setCaption("Fecha Visado");
		    visado.setValue(Utils.formatoFecha(sol.getFechaVisado()));
		    fl.addComponent(visado);
	    }
	    
	    if(sol.getFechaEnvioCliente() != null){
		    Label envioCl = new Label();
		    envioCl.setCaption("Fecha Envio a Cliente");
		    envioCl.setValue(Utils.formatoFecha(sol.getFechaEnvioCliente()));
		    fl.addComponent(envioCl);
	    }
	    
	    Label spTerreno = new Label();
	    spTerreno.setCaption("Mts Superficie Terreno");
	    spTerreno.setValue(Float.toString(sol.getBien().getSuperficieTerreno()));
	    fl.addComponent(spTerreno);
	    
	    Label valorTerreno = new Label();
	    valorTerreno.setCaption("Valor UF Superficie Terreno");
	    valorTerreno.setValue(Float.toString(sol.getBien().getUfSuperficieTerreno()));
	    fl.addComponent(valorTerreno);
	    
	    Label spEdificado = new Label();
	    spEdificado.setCaption("Mts Superficie Edificado");
	    spEdificado.setValue(Float.toString(sol.getBien().getSuperficieConstruida()));
	    fl.addComponent(spEdificado);
	    
	    Label valorEdificado = new Label();
	    valorEdificado.setCaption("Valor UF Superficie TerEdificadoreno");
	    valorEdificado.setValue(Float.toString(sol.getBien().getUfSuperficieConstruida()));
	    fl.addComponent(valorEdificado);
	    
	    Label spTerraza = new Label();
	    spTerraza.setCaption("Superficie Balcón/Terraza");
	    spTerraza.setValue(Float.toString(sol.getBien().getSuperficieTerraza()));
	    fl.addComponent(spTerraza);
	    
	    Label valorTerraza = new Label();
	    valorTerraza.setCaption("Valor UF Superficie Balcón/Terraza");
	    valorTerraza.setValue(Float.toString(sol.getBien().getUfSuperficieTerraza()));
	    fl.addComponent(valorTerraza);
	    
	    Label sectionAdicionales = new Label("Adicionales");
	    sectionAdicionales.addStyleName(ValoTheme.LABEL_H3);
	    sectionAdicionales.addStyleName(ValoTheme.LABEL_COLORED);	    
	    fl.addComponent(sectionAdicionales);
	    
	    if(!sol.getBien().getObrasComplementarias().isEmpty()){
	    	Set<ObraComplementaria> obras = sol.getBien().getObrasComplementarias();
	    	for(ObraComplementaria o : obras){
    		   Label obra = new Label();
    		   obra.setCaption(o.getAdicional().name());
    		   obra.setValue("Superficie "+Long.toString(o.getCantidadSuperficie())+", Valor UF "+Long.toString(o.getValorTotalUF()));
    		   fl.addComponent(obra);
	    	}	    	
	    }else{
	    	Label mensaje1 = new Label();
	    	mensaje1.setValue("No registra adicionales.");
	  	    fl.addComponent(mensaje1);
	    }
	    
	    Label sectionPRograma = new Label("Programa");
	    sectionPRograma.addStyleName(ValoTheme.LABEL_H3);
	    sectionPRograma.addStyleName(ValoTheme.LABEL_COLORED);	    
	    fl.addComponent(sectionPRograma);

	    if(!sol.getBien().getProgramas().isEmpty()){
	    	Set<ProgramaBien> programas = sol.getBien().getProgramas();
	    	for(ProgramaBien p : programas){
    		   Label programa = new Label();
    		   programa.setCaption(p.getPrograma().name());
    		   programa.setValue("Cantidad/Superficie "+Float.toString(p.getCantidadSuperficie()));
    		   fl.addComponent(programa);
	    	}	    	
	    }else{
	    	Label mensaje1 = new Label();
	    	mensaje1.setValue("No registra programas.");
	  	    fl.addComponent(mensaje1);
	    }
	    
	    Label sectionUbicacion = new Label("Ubicación");
	    sectionUbicacion.addStyleName(ValoTheme.LABEL_H3);
	    sectionUbicacion.addStyleName(ValoTheme.LABEL_COLORED);
	    fl.addComponent(sectionUbicacion);
	    
	    vl.addComponent(buildUbicacion(sol));
	    
	    vl.addComponent(buildFooter());
		
	    window.setContent(vl);
		UI.getCurrent().addWindow(window);

		return window;
	}
    
	 private HorizontalLayout buildFooter() {
		 
	    HorizontalLayout footer = new HorizontalLayout();
	    footer.setSpacing(true);
	    footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
	    footer.setWidth(100.0f, Unit.PERCENTAGE);
	    
	    Button btnCerrar = new Button("Cerrar", FontAwesome.CLOSE);
	    btnCerrar.addStyleName(ValoTheme.BUTTON_PRIMARY);	    
	    btnCerrar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				((UI)window.getParent()).removeWindow(window);
			}
		});
	    
	    footer.addComponent(btnCerrar);
	    footer.setComponentAlignment(btnCerrar, Alignment.BOTTOM_RIGHT);
	    
	    return footer;
	}
	
	 private VerticalLayout buildUbicacion(SolicitudTasacion sol){		
		VerticalLayout vl = new VerticalLayout();
		vl.setCaption("Mapa");
		vl.setSizeFull();
		
		googleMap = new GoogleMap(apiKey, null, "english");
		googleMap.setCenter(new LatLon(sol.getNorteY(), sol.getEsteX()));
		googleMap.addMarker(sol.getEstado().name(), new LatLon(sol.getNorteY(), sol.getEsteX()), false, "VAADIN/img/pin_tas_asignada.png");
		googleMap.setSizeFull();	
		googleMap.setZoom(12);
		
		vl.addComponent(googleMap);
		
		return vl;
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
