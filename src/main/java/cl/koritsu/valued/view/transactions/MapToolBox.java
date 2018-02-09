package cl.koritsu.valued.view.transactions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.tepi.filtertable.FilterTable;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Transaction;
import cl.koritsu.valued.event.ValuedEvent.BrowserResizeEvent;
import cl.koritsu.valued.event.ValuedEvent.TransactionReportEvent;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.view.ValuedViewType;
import cl.koritsu.valued.view.utils.Utils;

public class MapToolBox extends Window {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1172954522393568753L;
    private static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
    private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
            "theater", "room", "title", "seats" };
	
    /*contenedor para la lista de tasaciones y su tabla*/
    BeanItemContainer<SolicitudTasacion> solicitudContainer = new BeanItemContainer<SolicitudTasacion>(SolicitudTasacion.class);
	FilterTable table;
	/*editor de tasaciones*/
	EditorSolicitudTasacion editorSolicitud;


    
    /** CODIGO PARA AGREGAR LISTENER DEL BOTON DE TASACIONES */
    List<OnClickTasacionEvent> onClickTasacionEvents = new ArrayList<OnClickTasacionEvent>();
    public void addOnClickTasacionEvent(OnClickTasacionEvent listener) {
    	onClickTasacionEvents.add(listener);
    }
    private void doClickTasacion(BeanItem<SolicitudTasacion> sol) {
    	for(OnClickTasacionEvent listener : onClickTasacionEvents) {
    		listener.onClick(sol);
    	}
    }
	public interface OnClickTasacionEvent {
		void onClick(BeanItem<SolicitudTasacion> sol);
	}
	/** FIN CODIGO PARA AGREGAR LISTENER DEL BOTON DE TASACIONES */
    

	public MapToolBox() {
	      setClosable(false);
	      setResizable(true);
	      setPosition(210, 220);
	      setWidth("550px");
	      setHeight("520px");
	      
	       //mapToolBox.addStyleName("mywindowstyle");
	      
	      //por defecto crea la tabla que tendrá las tasaciones
	      table = buildTable();
	      setContent(table);
	      //crea el formulario que podrà utilizar el tasador para llenar la información
	      editorSolicitud = new EditorSolicitudTasacion();
	      
//	      define que la ventana no sea cerrada por los eventos del bus
	      setData("no_cerrar");
          
	}
	
	private FilterTable buildTable() {
    	FilterTable table = new FilterTable()
    	{
            @Override
            protected String formatPropertyValue(final Object rowId,
                    final Object colId, final Property<?> property) {
                String result = super.formatPropertyValue(rowId, colId,
                        property);
                if (colId.equals("fechaEncargo")) {
                    result = Utils.formatoFecha(((Date) property.getValue()));
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
    	table.setImmediate(true);
        table.setContainerDataSource(solicitudContainer);
        table.setSizeFull();
//        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
//        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
//        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setSelectable(true);
        table.setColumnReorderingAllowed(true);
        table.setSortAscending(false);
        
        table.addGeneratedColumn("nombrecliente", new CustomTable.ColumnGenerator() {

			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId)).getBean();
				return sol.getCliente() != null ? sol.getCliente().getNombreCliente() : "";
			}
		});
        
        table.addGeneratedColumn("direccion", new CustomTable.ColumnGenerator() {

			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId)).getBean();
				Bien bien = sol.getBien();				
				return (bien != null && bien.getDireccion() != null)
						? bien.getDireccion() + " " + bien.getNumeroManzana() + " " + bien.getComuna().getNombre() + " "
								+ bien.getComuna().getRegion().getNombre()
						: "";
			}
		});

        table.addGeneratedColumn("acceder", new CustomTable.ColumnGenerator()  {

			@Override
			public Object generateCell(final CustomTable source, final Object itemId, Object columnId) {
				Button editarTasacion = new Button(null,FontAwesome.MAP_MARKER);
				editarTasacion.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						BeanItem<SolicitudTasacion> sol = ((BeanItem<SolicitudTasacion>)source.getItem(itemId));
						//cambia la vista para mostrar el formulario de modificaciòn de solicitudes
						setSolTasacion(sol.getBean());
						doClickTasacion(sol);

					}
				});
				return editarTasacion;
			}
        });
        
        table.setVisibleColumns("acceder","numeroTasacion","fechaEncargo","direccion");
        table.setColumnHeaders("Acceder","N° Tasación", "Fecha Encargo","Dirección");
        table.setFilterBarVisible(true);
        table.setFooterVisible(true);

        return table;
    }
	
	/**
	 * Define la solicitud de tasacion que se modificará
	 * @param sol
	 */
	public void setSolTasacion(SolicitudTasacion sol) {
		
		setContent(editorSolicitud);
		editorSolicitud.setSolicitud(sol);
		
	}
	
	    
    
    private void regresar() {
    	
    	setContent(table);
//    	googleMap.clearMarkers();
//		mapToolBox.setContent(table);
//      	googleMap.setCenter(new LatLon(-33.448779, -70.668551));
    }
    
    private boolean defaultColumnsVisible() {
        boolean result = true;
        for (String propertyId : DEFAULT_COLLAPSIBLE) {
            if (table.isColumnCollapsed(propertyId) == Page.getCurrent()
                    .getBrowserWindowWidth() < 800) {
                result = false;
            }
        }
        return result;
    }

    @Subscribe
    public void browserResized(final BrowserResizeEvent event) {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.
        if (defaultColumnsVisible()) {
            for (String propertyId : DEFAULT_COLLAPSIBLE) {
                table.setColumnCollapsed(propertyId, Page.getCurrent()
                        .getBrowserWindowWidth() < 800);
            }
        }
    }
    
    void createNewReportFromSelection() {
        UI.getCurrent().getNavigator()
                .navigateTo(ValuedViewType.REPORTS.getViewName());
        ValuedEventBus.post(new TransactionReportEvent(
                (Collection<Transaction>) table.getValue()));
    }
	public void setSolicitudes(List<SolicitudTasacion> solicitudes) {
		table.removeAllItems();
    	((BeanItemContainer<SolicitudTasacion>)table.getContainerDataSource()).addAll(solicitudes);
	}
	
}
