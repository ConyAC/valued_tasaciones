package cl.koritsu.valued.view.transactions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Transaction;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.domain.enums.Permiso;
import cl.koritsu.valued.event.ValuedEvent.BrowserResizeEvent;
import cl.koritsu.valued.event.ValuedEvent.TransactionReportEvent;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.view.ValuedViewType;
import cl.koritsu.valued.view.transactions.EditorSolicitudTasacion.OnClickGuardarListener;
import cl.koritsu.valued.view.transactions.EditorSolicitudTasacion.OnClickRegresarListener;
import cl.koritsu.valued.view.transactions.EditorSolicitudTasacion.OnClickSiguienteListener;
import cl.koritsu.valued.view.utils.SecurityHelper;
import cl.koritsu.valued.view.utils.Utils;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;


public class MapToolBox extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1172954522393568753L;
	private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
			"theater", "room", "title", "seats" };

	/* contenedor para la lista de tasaciones y su tabla */
	BeanItemContainer<SolicitudTasacion> solicitudContainer = new BeanItemContainer<SolicitudTasacion>(
			SolicitudTasacion.class);
	FilterTable table;
	/* editor de tasaciones */
	EditorSolicitudTasacion editorSolicitud;

	/** CODIGO PARA AGREGAR LISTENER DEL BOTON DE TASACIONES */
	// enviar correo
	List<OnClickTasacionEvent> onClickEmailEvents = new ArrayList<OnClickTasacionEvent>();

	public void sendEmailOnClickEvent(OnClickTasacionEvent listener) {
		onClickEmailEvents.add(listener);
	}

	private void doClickEmail(BeanItem<SolicitudTasacion> sol) {
		for (OnClickTasacionEvent listener : onClickEmailEvents) {
			listener.onClick(sol);
		}
	}
	
	// seleccionar tasaciones
	List<OnClickTasacionEvent> onClickTasacionEvents = new ArrayList<OnClickTasacionEvent>();

	public void addOnClickTasacionEvent(OnClickTasacionEvent listener) {
		onClickTasacionEvents.add(listener);
	}

	private void doClickTasacion(BeanItem<SolicitudTasacion> sol) {
		for (OnClickTasacionEvent listener : onClickTasacionEvents) {
			listener.onClick(sol);
		}
	}

	public interface OnClickTasacionEvent {
		void onClick(BeanItem<SolicitudTasacion> sol);
	}

	// evento regresar
	List<OnClickRegresarListener> onClickRegresarEvents = new ArrayList<OnClickRegresarListener>();

	public void addOnClickRegresarEvent(OnClickRegresarListener listener) {
		onClickRegresarEvents.add(listener);
	}

	private void doClickRegresar(BeanItem<SolicitudTasacion> sol) {
		for (OnClickRegresarListener listener : onClickRegresarEvents) {
			listener.onClick(sol);
		}
	}

	// evento guardar
	List<OnClickSiguienteListener> onClickSiguienteEvents = new ArrayList<OnClickSiguienteListener>();

	public void addOnClickSiguienteEvent(OnClickSiguienteListener listener) {
		onClickSiguienteEvents.add(listener);
	}

	private void doClickSiguiente(BeanItem<SolicitudTasacion> sol) {
		for (OnClickSiguienteListener listener : onClickSiguienteEvents) {
			listener.onClick(sol);
		}
	}

	/** FIN CODIGO PARA AGREGAR LISTENER DEL BOTON DE TASACIONES */

	public MapToolBox() {
		setClosable(false);
		setResizable(true);
		setPosition(210, 220);
		setWidth("550px");
		setHeight("520px");

		// mapToolBox.addStyleName("mywindowstyle");

		// por defecto crea la tabla que tendrá las tasaciones
		table = buildTable();
		setContent(table);
		// crea el formulario que podrà utilizar el tasador para llenar la
		// información
		editorSolicitud = new EditorSolicitudTasacion();
		editorSolicitud.addOnClickRegresarEvent(new OnClickRegresarListener() {

			@Override
			public void onClick(BeanItem<SolicitudTasacion> sol) {
				regresar(sol);
			}
		});
		editorSolicitud
				.addOnClickGuardarEvent(new OnClickGuardarListener() {
		
					@Override
					public void onClick(BeanItem<SolicitudTasacion> sol) {
						doClickSiguiente(sol);
						// si el estado es tasada y TODO es tasador, retorna a
						// la tabla
						solicitudContainer.getItem(sol.getBean())
								.getItemProperty("estado")
								.setValue(sol.getBean().getEstado());
					}
				});
		
		editorSolicitud
				.addOnClickSiguienteListener(new OnClickSiguienteListener() {

					@Override
					public void onClick(BeanItem<SolicitudTasacion> sol) {
						doClickSiguiente(sol);
						// si el estado es tasada y TODO es tasador, retorna a
						// la tabla
						solicitudContainer.getItem(sol.getBean())
								.getItemProperty("estado")
								.setValue(sol.getBean().getEstado());
						setContent(table);
					}
				});

		// define que la ventana no sea cerrada por los eventos del bus
		setData("no_cerrar");

	}

	private FilterTable buildTable() {
		final FilterTable table = new FilterTable() {
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
		
		solicitudContainer.addNestedContainerProperty("bien.direccion");
		solicitudContainer.addNestedContainerProperty("bien.comuna.nombre");
		solicitudContainer.addNestedContainerProperty("bien.comuna.region.nombre");
		table.setImmediate(true);
		table.setContainerDataSource(solicitudContainer);
		table.setSizeFull();
		// table.addStyleName(ValoTheme.TABLE_BORDERLESS);
		// table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		// table.addStyleName(ValoTheme.TABLE_COMPACT);
		table.setSelectable(true);
		table.setColumnReorderingAllowed(true);
		table.setSortAscending(false);

//		table.addGeneratedColumn("direccion",
//				new CustomTable.ColumnGenerator() {
//
//					@Override
//					public Object generateCell(CustomTable source,
//							Object itemId, Object columnId) {
//						SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId)).getBean();
//						Bien bien = sol.getBien();
//						return (bien != null && bien.getDireccion() != null) ? bien
//								.getDireccion()
//								+ " "
//								+ bien.getNumeroManzana()
//								+ " "
//								+ bien.getComuna().getNombre()
//								+ " "
//								+ bien.getComuna().getRegion().getNombre()
//								: "";
//					}
//				});
		
		table.addGeneratedColumn("acciones", new CustomTable.ColumnGenerator() {
			
			@Override
			public Object generateCell(final CustomTable source, final Object itemId,Object columnId) {
				HorizontalLayout hl = new HorizontalLayout();
				
				if( SecurityHelper.hasPermission(Permiso.VISUALIZAR_TASACIONES) || SecurityHelper.hasPermission(Permiso.VISUALIZAR_MIS_TASACIONES)){
					Button editarTasacion = new Button(null, FontAwesome.MAP_MARKER);
					editarTasacion.addClickListener(new Button.ClickListener() {
	
						@Override
						public void buttonClick(ClickEvent event) {
							BeanItem<SolicitudTasacion> sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId));
							// cambia la vista para mostrar el formulario de
							// modificaciòn de solicitudes
							setSolTasacion(sol.getBean());
							doClickTasacion(sol);
	
						}
					});
					
					hl.addComponent(editarTasacion);
				}
				
				if( SecurityHelper.hasPermission(Permiso.ENVIAR_CORREO)){
					hl.setSpacing(true);						
						
					Button enviarCorreo = new Button(null,FontAwesome.SEND);
					enviarCorreo.addClickListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {	
							ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro que desea enviar correo alertando tardanza en el registro de la visita?",
							        "Si", "Cancelar", new ConfirmDialog.Listener() {

							            public void onClose(ConfirmDialog dialog) {
							                if (dialog.isConfirmed()) {
							                	BeanItem<SolicitudTasacion> sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId));
							                	if(sol.getBean() != null && sol.getBean().getTasador() != null && sol.getBean().getTasador().getEmail() != null) {
							                		doClickEmail(sol);
							                		Notification.show("Se ha enviado el correo al tasador: "+sol.getBean().getTasador().getFullname());
							                	}else
							                		Notification.show("No se ha podido enviar el correo ya que no existe email ingresado para el repsonsable de la tasación.");
							                } else {
							                    // User did not confirm
							                   ;
							                }
							            }
							        });		
							}
					});
					hl.addComponent(enviarCorreo);
					
					
					Date fechaTasacion = table.getItem(itemId)
							.getItemProperty("fechaTasacion").getValue() != null ? (Date) table
							.getItem(itemId).getItemProperty("fechaTasacion")
							.getValue()
							: new Date();
							
					if (fechaTasacion.before(new Date()) && table.getItem(itemId).getItemProperty("estado").getValue().equals(EstadoSolicitud.AGENDADA))
						enviarCorreo.setEnabled(true);
					else
						enviarCorreo.setEnabled(false);
				}
				return hl;
			}
		});
		
		if( SecurityHelper.hasPermission(Permiso.VISUALIZAR_TASACIONES) ){
			
		table.addGeneratedColumn("nombrecliente",
				new CustomTable.ColumnGenerator() {

					@Override
					public Object generateCell(CustomTable source,
							Object itemId, Object columnId) {
						SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>) source
								.getItem(itemId)).getBean();
						return sol.getCliente() != null ? sol.getCliente()
								.getNombreCliente() : "";
					}
				});

			table.addGeneratedColumn("tasador",
				new CustomTable.ColumnGenerator() {

					@Override
					public Object generateCell(CustomTable source,
							Object itemId, Object columnId) {
							SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId)).getBean();
							return sol.getTasador() != null ? sol.getTasador().getFullname() : "";
					}
				});
		}

		if( SecurityHelper.hasPermission(Permiso.VISUALIZAR_TASACIONES)){
			table.setVisibleColumns("acciones", "estado", "fechaEncargo", "fechaTasacion", "tasador", "bien.direccion","bien.comuna.nombre","bien.comuna.region.nombre","numeroTasacion","nombrecliente");
			table.setColumnHeaders("Acciones", "Estado","Fecha Encargo", "Fecha Visista", "Tasador","Dirección","Comuna","Región","N° Tasación","Cliente");
		}else{
			table.setVisibleColumns("acciones", "estado", "fechaEncargo", "fechaTasacion", "bien.direccion","bien.comuna.nombre","bien.comuna.region.nombre","numeroTasacion");
			table.setColumnHeaders("Acceder", "Estado","Fecha Encargo", "Fecha Visita", "Dirección", "Comuna","Región","N° Tasación");
			}

		table.setFilterBarVisible(true);
		table.setFooterVisible(true);

		table.setCellStyleGenerator(new FilterTable.CellStyleGenerator() {
			@Override
			public String getStyle(CustomTable source, Object itemId,
					Object propertyId) {

				Date fechaTasacion = table.getItem(itemId)
						.getItemProperty("fechaTasacion").getValue() != null ? (Date) table
						.getItem(itemId).getItemProperty("fechaTasacion")
						.getValue()
						: new Date();
						
				if (fechaTasacion.before(new Date()) && table.getItem(itemId).getItemProperty("estado").getValue().equals(EstadoSolicitud.AGENDADA))
					return "visita-pendiente";
				else
					return "";
			}
		});

		return table;
	}

	/**
	 * Define la solicitud de tasacion que se modificará
	 * 
	 * @param sol
	 */
	public void setSolTasacion(SolicitudTasacion sol) {

		setContent(editorSolicitud);
		editorSolicitud.setSolicitud(sol);

	}
	
	/**
	 * Permite setear las coordenadas del mapa toda vez que se arrastre el punto
	 * @param coord
	 */
	public void setCoordenadasTasacion(String coord) {
		editorSolicitud.setCoordenadas(coord);

	}

	/**
	 * Permite regresar
	 * 
	 * @param sol
	 */
	private void regresar(BeanItem<SolicitudTasacion> sol) {

		setContent(table);
		doClickRegresar(sol);

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

	/**
	 * Permite llenar la tabla de solicitudes del toolbox
	 * 
	 * @param solicitudes
	 */
	public void setSolicitudes(List<SolicitudTasacion> solicitudes) {
		table.removeAllItems();
		((BeanItemContainer<SolicitudTasacion>) table.getContainerDataSource())
				.addAll(solicitudes);
	}

}
