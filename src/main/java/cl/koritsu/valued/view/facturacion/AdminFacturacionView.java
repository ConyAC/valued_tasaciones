package cl.koritsu.valued.view.facturacion;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.dialogs.ConfirmDialog;

import ru.xpoft.vaadin.VaadinView;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Factura;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.enums.EstadoFactura;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.utils.Utils;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
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

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = AdminFacturacionView.NAME, cached = true)
public class AdminFacturacionView extends CssLayout implements View {
	
	public static final String NAME = "adminfacturacion";

	protected BeanFieldGroup<Factura> fieldGroup = new BeanFieldGroup<Factura>(Factura.class);
	protected BeanItemContainer<SolicitudTasacion> solicitudContainer = new BeanItemContainer<SolicitudTasacion>(
			SolicitudTasacion.class);
	
	ComboBox cbCliente, cbRegion, cbComuna, cbDireccion;
	List<Factura> facturas;
	Table tableTasaciones = new Table();
	String facturaID;
	
	@Autowired
	ValuedService service;
	
    public AdminFacturacionView() {
    	
	}

	@PostConstruct
	public void init() {
		Responsive.makeResponsive(this);
		setSizeFull();

		addComponent(buildFormFactura());

		addComponent(buildFooter());

        fieldGroup.bindMemberFields(this);
	}

	/*
	 * Permite la creación del formulario base para ingresar facturas.
	 */
	private FormLayout buildFormFactura() {
		FormLayout fl = new FormLayout();
		fl.setMargin(true);

		Label sectionInformacion = new Label("Información Factura");
		sectionInformacion.addStyleName(ValoTheme.LABEL_H3);
		sectionInformacion.addStyleName(ValoTheme.LABEL_COLORED);
		fl.addComponent(sectionInformacion);

		TextField nombre = new TextField();
		nombre.setCaption("Nombre Factura");
		fieldGroup.bind(nombre, "nombre");
		fl.addComponent(nombre);

		TextField numero = new TextField();
		numero.setCaption("N° Factura");
		numero.setRequired(true);
		numero.setRequiredError("El numero de factura es obligatorio.");
		fieldGroup.bind(numero, "numero");
		fl.addComponent(numero);
		
		BeanItemContainer<Cliente> cls = new BeanItemContainer<Cliente>(
				Cliente.class);
		List<Cliente> clientes = service.getClientes();
		cls.addAll(clientes);
		
		ComboBox cbCliente = new ComboBox("Cliente",cls);
		cbCliente.setRequired(true);
		cbCliente.setRequiredError("El cliente es obligatorio.");
		cbCliente.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbCliente.setItemCaptionPropertyId("fullname");
		fieldGroup.bind(cbCliente, "cliente");
		fl.addComponent(cbCliente);

		DateField fecha = new DateField();
		fecha.setRequired(true);
		fecha.setRequiredError("La fecha es obligatoria.");
		fecha.setCaption("Fecha");
		fieldGroup.bind(fecha, "fecha");
		fl.addComponent(fecha);

		ComboBox cbEstado = new ComboBox("Estado");
		cbEstado.setImmediate(true);
		fieldGroup.bind( cbEstado, "estado");
		for (EstadoFactura estado : EstadoFactura.values()) {
			cbEstado.addItem(estado);
		}
		fl.addComponent(cbEstado);

		TextField montoManual = new TextField();
		montoManual.setRequired(true);
		montoManual.setRequiredError("Debe ingresar un monto en pesos.");
		montoManual.setCaption("Monto Manual");
		fieldGroup.bind( montoManual, "montoManual");
		fl.addComponent(montoManual);

		Label sectionTasaciones = new Label("Información Tasaciones");
		sectionTasaciones.addStyleName(ValoTheme.LABEL_H3);
		sectionTasaciones.addStyleName(ValoTheme.LABEL_COLORED);
		fl.addComponent(sectionTasaciones);

		fl.addComponent(buildTableTasaciones());

		return fl;
	}

	/*
	 * Permite la creación de la tabla de tasaciones asociadas a la factura.
	 */
	private HorizontalLayout buildTableTasaciones() {
		HorizontalLayout root = new HorizontalLayout();
		root.setMargin(false);
		root.setSpacing(false);
		root.setWidth("100%");

		FormLayout details = new FormLayout();
		details.setMargin(false);
		details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		root.addComponent(details);
		root.setExpandRatio(details, 1);

		Button btnAddTasacion = new Button(null, FontAwesome.PLUS);
		details.addComponent(btnAddTasacion);
		details.setComponentAlignment(btnAddTasacion, Alignment.TOP_RIGHT);

		btnAddTasacion.addClickListener(new Button.ClickListener() {
		
			 @Override
			 public void buttonClick(ClickEvent event) {
				 drawFormAddTasaciones();
			 }
		 });

		final Table tabTasaciones = new Table() {
			@Override
			protected String formatPropertyValue(final Object rowId,
					final Object colId, final Property<?> property) {
				String result = super.formatPropertyValue(rowId, colId,
						property);
				if (colId.equals("fechaEncargo")) {
					result = Utils.formatoFecha(((Date) property.getValue()));
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

		tabTasaciones.setPageLength(3);
		tabTasaciones.setWidth("100%");
		tabTasaciones.setImmediate(true);

		solicitudContainer.addNestedContainerProperty("bien.direccion");
		solicitudContainer.addNestedContainerProperty("bien.comuna.nombre");
		solicitudContainer.addNestedContainerProperty("bien.comuna.region.nombre");

		tabTasaciones.addGeneratedColumn("acciones", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(final Table source,
					final Object itemId, Object columnId) {
				return new Button(null, new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(
								UI.getCurrent(),
								"Confirmar Acción:","¿Está seguro de eliminar la tasación seleccionada?","Eliminar", "Cancelar",
								new ConfirmDialog.Listener() {

									public void onClose(
											ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											BeanItem<SolicitudTasacion> sol = ((BeanItem<SolicitudTasacion>) source.getItem(itemId));
									    	fieldGroup.getItemDataSource().getBean().getSolicitudes().remove(sol.getBean());
											solicitudContainer.removeItem(itemId);
										}
									}
								});
					}
				}) {
					{
						setIcon(FontAwesome.TRASH_O);
					}
				};
			}
		});

		tabTasaciones.addGeneratedColumn("tasador",	new Table.ColumnGenerator() {

					@Override
					public Object generateCell(Table source, Object itemId,
							Object columnId) {
						SolicitudTasacion sol = ((BeanItem<SolicitudTasacion>) source
								.getItem(itemId)).getBean();
						return sol.getTasador() != null ? sol.getTasador()
								.getFullname() : "";
					}
				});

		tabTasaciones.setContainerDataSource(solicitudContainer);
		tabTasaciones.setVisibleColumns("numeroTasacion", "fechaEncargo","bien.direccion", "bien.comuna.nombre","bien.comuna.region.nombre", "tasador", "acciones");
		tabTasaciones.setColumnHeaders("N° Tasación", "Fecha Encargo","Dirección", "Comuna", "Región", "Tasador", "Acciones");
		tabTasaciones.setEditable(false);
		details.addComponent(tabTasaciones);

		return root;
	}

	/*
	 * Permite desplegar la ventana emergente correspondiente a la busqueda y selección de tasaciones.
	 */
	private Window drawFormAddTasaciones() {
		final EditarTasaciones editor;
		editor = new EditarTasaciones(service,fieldGroup,solicitudContainer);

//		editor.getBtnCancelar().addClickListener(new ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				editor.fieldGroup.discard();
//			}
//		});

		editor.getBtnGuadar().addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					// realiza el commit de los campos del fomulario
					editor.fieldGroup.commit();
					Notification.show("Tasaciones almacenadas correctamente.", Type.TRAY_NOTIFICATION);
				} catch (CommitException e) {
					Utils.validateEditor("el cliente", e);
				}
			}
		});
		
		solicitudContainer.addAll(editor.fieldGroup.getItemDataSource().getBean().getSolicitudes());
		return editor;
	}

	/*
	 * Permite la creación de los botones que almacenan o cancelan la modificación.
	 */
	private HorizontalLayout buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.setSpacing(true);
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

		Button btnGuardar = new Button("Guardar");
		btnGuardar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					fieldGroup.commit();
					BeanItem<Factura> factura = fieldGroup.getItemDataSource();
					service.saveFactura(factura.getBean());
					
					Notification.show("Factura almacenada correctamente.", Type.WARNING_MESSAGE);
					
					UI.getCurrent().getNavigator().navigateTo(FacturacionView.NAME);
				} catch (CommitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnGuardar.setIcon(FontAwesome.SAVE);
		btnGuardar.addStyleName(ValoTheme.BUTTON_PRIMARY);

		Button btnCancelar = new Button("Cancelar");
		btnCancelar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().getNavigator().navigateTo(FacturacionView.NAME);
			}
		});

		btnCancelar.addStyleName("link");

		footer.addComponent(btnGuardar);
		footer.addComponent(btnCancelar);
		
		footer.setComponentAlignment(btnGuardar, Alignment.TOP_RIGHT);
		footer.setComponentAlignment(btnCancelar, Alignment.TOP_RIGHT);

		return footer;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		String args[] = event.getParameters().split("/");
		facturaID = args[0];
		solicitudContainer.removeAllItems();
		
		if(facturaID != null && !facturaID.equals("new")){
			Factura f = service.getFacturaById(Long.parseLong(facturaID));
			solicitudContainer.addAll(f.getSolicitudes());
			fieldGroup.setItemDataSource(f);
		}else{
			fieldGroup.setItemDataSource(new Factura());
		}		
	}
}
