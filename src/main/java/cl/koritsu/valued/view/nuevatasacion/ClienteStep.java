package cl.koritsu.valued.view.nuevatasacion;

import java.util.List;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Contacto;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.Solicitante;
import cl.koritsu.valued.domain.Sucursal;
import cl.koritsu.valued.domain.TipoOperacion;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.nuevatasacion.vo.NuevaSolicitudVO;
import cl.koritsu.valued.view.utils.Utils;


public class ClienteStep implements WizardStep {
	
	FormLayout testLayout, form;
	VerticalLayout generalDetailLayout, ejecutivoDetailLayout;
	HorizontalSplitPanel hsp;
	Button btnEjecutivo, btnSucursal,btnAgregarCliente;
	BeanFieldGroup<NuevaSolicitudVO> fg;
	ValuedService service;

	public ClienteStep(BeanFieldGroup<NuevaSolicitudVO> fg,ValuedService service) {
		this.fg = fg;
		this.service = service;
	}

	@Override
	public String getCaption() {
		return "Cliente";
	}
	
	@Override
	public Component getContent() {
		
		hsp = new HorizontalSplitPanel();
		hsp.setSizeFull();
		
		VerticalLayout usersListLayout = drawForm();
		hsp.addComponent(usersListLayout);
				
		return hsp;
	}
	
	private VerticalLayout drawFormAddCliente() {

		final ClienteEditor editor = new ClienteEditor();
		editor.getBtnCancelar().addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				hsp.removeComponent(generalDetailLayout);	
				btnAgregarCliente.setEnabled(true);   
				editor.fieldGroup.discard();
			}
		});
		
		editor.getBtnGuadar().addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					hsp.removeComponent(generalDetailLayout);	
					editor.fieldGroup.commit();
	        		service.saveCliente(editor.fieldGroup.getItemDataSource().getBean());
	        		Notification.show("Sucursal guardada correctamente",Type.TRAY_NOTIFICATION);
				} catch (CommitException e) {

					e.printStackTrace();
	        		Notification.show("Error al guardar la sucursal debido a "+e.getMessage(),Type.TRAY_NOTIFICATION);
				}
			}
		});
		
		return editor;
	}

	/*
	 * Permite dibujar el formulario de nuevo ingreso para sucursal
	 */
	private VerticalLayout drawFormAddSucursal() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
		FormLayout detailLayout = new FormLayout();
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		
		Panel p = new Panel(detailLayout);
		p.setCaption("Creando nueva Sucursal");
		p.setSizeFull();
		vl.addComponent(p);
		vl.setExpandRatio(p, 1.0f);
		
		final BeanFieldGroup<Sucursal> fgSucursal = new BeanFieldGroup<Sucursal>(Sucursal.class);
		fgSucursal.setItemDataSource(new Sucursal());

		// Loop through the properties, build fields for them and add the fields
        // to this UI 
		TextField tfNombre = new TextField("Nombre");
		Utils.bind(fgSucursal, tfNombre, "nombre");
		tfNombre.setNullRepresentation("");
		detailLayout.addComponent(tfNombre);

		final ComboBox cbRegion = new ComboBox("Región");
		cbRegion.setContainerDataSource(new BeanItemContainer<Region>(Region.class));
		cbRegion.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbRegion.setItemCaptionPropertyId("nombre");
		List<Region> regiones = service.getRegiones();
		((BeanItemContainer<Region>)cbRegion.getContainerDataSource()).addAll(regiones);
		
		cbRegion.setWidth("100%");
		detailLayout.addComponent(cbRegion);
		
		final ComboBox cbComuna = new ComboBox("Comuna");
		cbComuna.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbComuna.setItemCaptionPropertyId("nombre");
		Utils.bind(fgSucursal, cbComuna, "comuna");
		cbComuna.setEnabled(false);
		
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
		
		cbComuna.setWidth("100%");
		detailLayout.addComponent(cbComuna);
		
		TextField tfDireccion = new TextField("Dirección");
		Utils.bind(fgSucursal, tfDireccion, "direccion");
		tfDireccion.setNullRepresentation("");
		detailLayout.addComponent(tfDireccion);

		HorizontalLayout botones = new HorizontalLayout();
		botones.setHeight("60px");
		botones.setSpacing(true);
		
		//agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {

        		try {
					fgSucursal.commit();
	        		service.saveSucursal(fgSucursal.getItemDataSource().getBean());
	        		Notification.show("Sucursal guardada correctamente",Type.TRAY_NOTIFICATION);
				} catch (CommitException e) {

					e.printStackTrace();
	        		Notification.show("Error al guardar la sucursal debido a "+e.getMessage(),Type.TRAY_NOTIFICATION);
				}
        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        
        botones.addComponent(btnSave);
        botones.setComponentAlignment(btnSave, Alignment.BOTTOM_LEFT);
        
      //agrega un boton que cencela acción
        Button btnCancel = new Button("Cancelar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		hsp.removeComponent(generalDetailLayout);	
        		btnSucursal.setEnabled(true);   
        		fgSucursal.discard();
        	}
        }){{
        	addStyleName("link");
        }};
        
        botones.addComponent(btnCancel);
        botones.setComponentAlignment(btnCancel, Alignment.BOTTOM_RIGHT);
        detailLayout.addComponent(botones);
        
		return vl;
	}
	
	/*
	 * Permite dibujar el formulario de nuevo ingreso para ejecutivo
	 */
	private VerticalLayout drawFormAddEjecutivo() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
		FormLayout detailLayout = new FormLayout();
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		
		Panel p = new Panel(detailLayout);
		p.setCaption("Creando nuevo ejecutivo");
		p.setSizeFull();
		vl.addComponent(p);
		vl.setExpandRatio(p, 1.0f);

		// Loop through the properties, build fields for them and add the fields
        // to this UI 
		TextField tfNombre = new TextField("Nombre");
		tfNombre.setNullRepresentation("");
		detailLayout.addComponent(tfNombre);
		
		TextField tfApellidos = new TextField("Apellidos");
		tfApellidos.setNullRepresentation("");
		detailLayout.addComponent(tfApellidos);
		
		TextField tfFono = new TextField("Teléfono Fijo");
		tfFono.setNullRepresentation("");
		detailLayout.addComponent(tfFono);
		
		TextField tfMovil= new TextField("Teléfono Móvil");
		tfMovil.setNullRepresentation("");
		detailLayout.addComponent(tfMovil);		

		TextField tfEmail = new TextField("Teléfono Email");
		tfEmail.setNullRepresentation("");
		detailLayout.addComponent(tfEmail);

		HorizontalLayout botones = new HorizontalLayout();
		botones.setHeight("60px");
		botones.setSpacing(true);
		
		//agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		Notification.show("Ejecutivo guardado correctamente",Type.TRAY_NOTIFICATION);
        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        
        botones.addComponent(btnSave);
        botones.setComponentAlignment(btnSave, Alignment.BOTTOM_LEFT);
        
      //agrega un boton que cencela acción
        Button btnCancel = new Button("Cancelar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		hsp.removeComponent(generalDetailLayout);	
        		btnEjecutivo.setEnabled(true); 
        	}
        }){{
        	addStyleName("link");
        }};
        
        botones.addComponent(btnCancel);
        botones.setComponentAlignment(btnCancel, Alignment.BOTTOM_RIGHT);
        detailLayout.addComponent(botones);
        
		return vl;
	}

	/*
	 * Permite dibujar el formulario de ingreso
	 */
	private VerticalLayout drawForm() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
		GridLayout gl = new GridLayout(3,10);
		gl.setSpacing(true);
		gl.setMargin(true);
                
		final ComboBox cbCliente = new ComboBox();
		cbCliente.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbCliente.setItemCaptionPropertyId("nombreCliente");
		BeanItemContainer<Cliente> ds = new BeanItemContainer<Cliente>(Cliente.class);
		List<Cliente> clientes = service.getClientes();
		ds.addAll(clientes);
		cbCliente.setContainerDataSource(ds);
		
		Utils.bind(fg,cbCliente, "cliente");
		
		// cliente
		gl.addComponents(new Label("Nombre Cliente"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				/*
				 * ejemplo de textfield autocomplete
				AutocompleteTextField  tf = new AutocompleteTextField ();
				
				tf.setCache(true); // Client side should cache suggestions
				tf.setDelay(150) ;// Delay before sending a query to the server
				tf.setItemAsHtml(false); // Suggestions contain html formating. If true, make sure that the html is save to use!
				tf.setMinChars(1); // The required value length to trigger a query
				tf.setScrollBehavior(ScrollBehavior.NONE); // The method that should be used to compensate scrolling of the page
				tf.setSuggestionLimit(5); // The max amount of suggestions send to the client. If the limit is >= 0 no limit is applied

				
				tf.setSuggestionProvider(new CollectionSuggestionProvider(theJavas, MatchMode.CONTAINS, true, Locale.US));
				*/

				//final Cliente cliente = getCurrentCliente();
				btnAgregarCliente = new Button(null,FontAwesome.PLUS_CIRCLE);				  
				btnAgregarCliente.addClickListener(
						new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								//ClienteWindow.open(cliente);								
								generalDetailLayout = drawFormAddCliente();	
								hsp.addComponent(generalDetailLayout);
								btnAgregarCliente.setEnabled(false);
							}

						});				
						
				addComponents(cbCliente,btnAgregarCliente);
			}
		});
		
		Label clienteSel = new Label();
		gl.addComponent(clienteSel);
		
		
		final ComboBox cbSucursal = new ComboBox();
		cbSucursal.setEnabled(false);
		cbSucursal.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbSucursal.setItemCaptionPropertyId("nombre");
		BeanItemContainer<Sucursal> ds2 = new BeanItemContainer<Sucursal>(Sucursal.class);
		cbSucursal.setContainerDataSource(ds2);
		
		Utils.bind(fg,cbSucursal, "sucursal");
		//sucursal
		gl.addComponents(new Label("Nombre Sucursal"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				
				btnSucursal = new Button(FontAwesome.PLUS_CIRCLE);
				btnSucursal.addClickListener(
						new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								generalDetailLayout = drawFormAddSucursal();	
								hsp.addComponent(generalDetailLayout);
								btnSucursal.setEnabled(false);
							}
						});	
			        
				addComponents(cbSucursal,btnSucursal);
			}
		});
		Label sucursalSel = new Label();
		gl.addComponent(sucursalSel);
		
		

		final ComboBox cbEjecutivo = new ComboBox();
		cbEjecutivo.setEnabled(false);
		cbEjecutivo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbEjecutivo.setItemCaptionPropertyId("nombre");
		BeanItemContainer<Contacto> ds3 = new BeanItemContainer<Contacto>(Contacto.class);
		cbEjecutivo.setContainerDataSource(ds3);
		Utils.bind(fg,cbEjecutivo, "ejecutivo");
		

		//ejecutivo
		gl.addComponents(new Label("Nombre Ejecutivo"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);

				btnEjecutivo = new Button(FontAwesome.PLUS_CIRCLE);
				btnEjecutivo.addClickListener(
						new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								generalDetailLayout = drawFormAddEjecutivo();	
								hsp.addComponent(generalDetailLayout);
								btnEjecutivo.setEnabled(false);
							}
						});	
				addComponents(cbEjecutivo,btnEjecutivo);
			}
		});
		Label ejecutivoSel = new Label();
		gl.addComponent(ejecutivoSel);
		
		
		final ComboBox cbSolicitante = new ComboBox();
		cbSolicitante.setEnabled(false);
		cbSolicitante.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbSolicitante.setItemCaptionPropertyId("nombre");
		BeanItemContainer<Solicitante> ds4 = new BeanItemContainer<Solicitante>(Solicitante.class);
		cbEjecutivo.setContainerDataSource(ds4);
		Utils.bind(fg,cbSolicitante, "solicitante");
		
		//solicitante
		gl.addComponents(new Label("Nombre Solicitante"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(cbSolicitante,btn);
			}
		});
		

		cbCliente.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Cliente cliente = (Cliente)event.getProperty().getValue();
				
				//carga las sucursales del cliente
				List<Sucursal> sucursal = service.getSucursalByCliente(cliente);
				cbSucursal.getContainerDataSource().removeAllItems();
				((BeanItemContainer<Sucursal>)cbSucursal.getContainerDataSource()).addAll(sucursal);
				cbSucursal.setEnabled(true);
				
				// carga el combobox de ejecutivos
				List<Contacto> ejecutivos = service.getEjecutivosByCliente(cliente);
				cbEjecutivo.getContainerDataSource().removeAllItems();
				((BeanItemContainer<Contacto>)cbEjecutivo.getContainerDataSource()).addAll(ejecutivos);
				cbEjecutivo.setEnabled(true);
				
				//carga el combo de solicitantes
				List<Solicitante> solicitantes = service.getSolicitantesByCliente(cliente);				
				cbSolicitante.getContainerDataSource().removeAllItems();
				((BeanItemContainer<Solicitante>)cbSolicitante.getContainerDataSource()).addAll(solicitantes);
				cbSolicitante.setEnabled(true);
			}
		});
		
		
		Label solicitanteSel = new Label();
		gl.addComponent(solicitanteSel);
		
		//tipo de operacion
		gl.addComponents(new Label("Tipo de Operación"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				tf.setItemCaptionMode(ItemCaptionMode.PROPERTY);
				tf.setItemCaptionPropertyId("nombre");
				tf.setContainerDataSource(new BeanItemContainer<TipoOperacion>(TipoOperacion.class));
				
				Utils.bind(fg,tf, "solicitudTasacion.tipoOperacion");
				List<TipoOperacion> operaciones = service.getOperaciones();
				int i = 0;
				for(TipoOperacion tipo : operaciones) {
					tf.addItem(tipo);
					if(i == 0)
						tf.setValue(tipo);
					i++;
				}
				addComponents(tf);
			}
		});
		
		vl.addComponent(gl);
		vl.setExpandRatio(gl, 1.0f);
		
		return vl;
	}
	
	@Override
	public boolean onAdvance() {
		return true;
	}

	@Override
	public boolean onBack() {
		return false;
	}
}
