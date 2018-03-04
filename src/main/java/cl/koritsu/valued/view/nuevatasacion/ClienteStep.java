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
import com.vaadin.shared.ui.combobox.FilteringMode;
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
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.Cargo;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Contacto;
import cl.koritsu.valued.domain.RazonSocial;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.Solicitante;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Sucursal;
import cl.koritsu.valued.domain.TipoOperacion;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.utils.Constants;
import cl.koritsu.valued.view.utils.Utils;


public class ClienteStep implements WizardStep {
	
	FormLayout testLayout, form;
	VerticalLayout generalDetailLayout, ejecutivoDetailLayout;
	HorizontalSplitPanel hsp;
	Button btnEjecutivo, btnSucursal,btnAgregarCliente,btnSolicitante;
	BeanFieldGroup<SolicitudTasacion> fg;
	ValuedService service;
	ComboBox cbCliente = new ComboBox();
	ComboBox cbSucursal = new ComboBox();
	ComboBox cbEjecutivo = new ComboBox();
	ComboBox cbSolicitante = new ComboBox();


	public ClienteStep(BeanFieldGroup<SolicitudTasacion> fg,ValuedService service) {
		this.fg = fg;
		this.service = service;
		
		initView();
	}
	
	private void initView() {
		hsp = new HorizontalSplitPanel();
		hsp.setSizeFull();
		
		VerticalLayout usersListLayout = drawForm();
		hsp.addComponent(usersListLayout);
	}

	@Override
	public String getCaption() {
		return "Cliente";
	}
	
	@Override
	public Component getContent() {
		return hsp;
	}
	
	private VerticalLayout drawFormAddCliente() {

		final ClienteEditor editor = new ClienteEditor(service);
		editor.getBtnCancelar().addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				hsp.removeComponent(generalDetailLayout);	
				editor.fieldGroup.discard();
				//habilita los botones para agregar
				enableButtons();
			}
		});
		
		editor.getBtnGuadar().addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					//realiza el commit de los campos del fomulario
					editor.fieldGroup.commit();
					// llena el objeto a guardar con los contactos;
					Cliente clienteToSave  = editor.fieldGroup.getItemDataSource().getBean();
					List<Contacto> contactos = editor.beanItemContacto.getItemIds();
					List<RazonSocial> razones = editor.beanItemRazon.getItemIds();
	        		service.saveCliente(clienteToSave,contactos,razones);
					hsp.removeComponent(generalDetailLayout);
					
					//recarga los clientes
					List<Cliente> clientes = service.getClientes();
					cbCliente.getContainerDataSource().removeAllItems();
					((BeanItemContainer<Cliente>) cbCliente.getContainerDataSource()).addAll(clientes);
					
					//habilita los botones para agregar otras cosas
					enableButtons();
					
	        		Notification.show("Cliente guardado correctamente",Type.TRAY_NOTIFICATION);
				} catch (CommitException e) {
					Utils.validateEditor("el cliente",e);
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
		tfNombre.setWidth("100%");
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
		cbComuna.setContainerDataSource(new BeanItemContainer<Comuna>(Comuna.class));
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
		tfDireccion.setWidth("100%");
		Utils.bind(fgSucursal, tfDireccion, "direccion");
		tfDireccion.setNullRepresentation("");
		detailLayout.addComponent(tfDireccion);

		HorizontalLayout botones = new HorizontalLayout();
		botones.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		botones.setSpacing(true);
		
		//agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {

        		try {
        			
					fgSucursal.commit();
					//setea
					Sucursal sucursal = fgSucursal.getItemDataSource().getBean();
					Cliente cliente = (Cliente) cbCliente.getValue();
					sucursal.setCliente(cliente);
	        		service.saveSucursal(sucursal);
	        		
					//recarga las sucursales del cliente
					List<Sucursal> sucursales = service.getSucursalByCliente(cliente);
					cbSucursal.getContainerDataSource().removeAllItems();
					((BeanItemContainer<Sucursal>) cbSucursal.getContainerDataSource()).addAll(sucursales);
					
	        		
	        		hsp.removeComponent(generalDetailLayout);	
	        		//habilita los botones para agregar
					enableButtons();
					
					Notification.show("Sucursal guardada correctamente",Type.TRAY_NOTIFICATION);
				} catch (CommitException e) {

					Utils.validateEditor("la sucursal",e);
				}
        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        
        botones.addComponent(btnSave);
        botones.setComponentAlignment(btnSave, Alignment.BOTTOM_LEFT);
        
      //agrega un boton que cencela acción
        Button btnCancel = new Button("Cancelar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		fgSucursal.discard();
        		hsp.removeComponent(generalDetailLayout);
        		
        		//habilita los botones para agregar
				enableButtons();
        	}
        }){{
        	addStyleName("link");
        }};
        
        botones.addComponent(btnCancel);
        botones.setComponentAlignment(btnCancel, Alignment.BOTTOM_RIGHT);
        vl.addComponent(botones);
        
		return vl;
	}
	
	/*
	 * Permite dibujar el formulario de nuevo ingreso para ejecutivo
	 */
	private VerticalLayout drawFormAddEjecutivo() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		
		final BeanFieldGroup<Contacto> fgEjecutivo = new BeanFieldGroup<Contacto>(Contacto.class);
		Contacto ejecutivo = new Contacto();
		ejecutivo.setCargo(new Cargo() {{setId(1L);}});
		ejecutivo.setCliente((Cliente) cbCliente.getValue());
		fgEjecutivo.setItemDataSource(ejecutivo);
		
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
		Utils.bind(fgEjecutivo, tfNombre,"nombre" );
		detailLayout.addComponent(tfNombre);
		
		TextField tfApellidoPaterno = new TextField("Apellido Paterno");
		Utils.bind(fgEjecutivo, tfApellidoPaterno,"apellidoPaterno" );
		tfApellidoPaterno.setNullRepresentation("");
		detailLayout.addComponent(tfApellidoPaterno);
		
		TextField tfApellidoMaterno = new TextField("Apellido Materno");
		Utils.bind(fgEjecutivo, tfApellidoMaterno,"apellidoMaterno" );
		tfApellidoMaterno.setNullRepresentation("");
		detailLayout.addComponent(tfApellidoMaterno);
		
		TextField tfFono = new TextField("Teléfono Fijo");
		Utils.bind(fgEjecutivo, tfFono,"telefonoFijo" );
		tfFono.setNullRepresentation("");
		detailLayout.addComponent(tfFono);
		
		TextField tfMovil= new TextField("Teléfono Móvil");
		Utils.bind(fgEjecutivo, tfMovil,"telefonoMovil" );
		tfMovil.setNullRepresentation("");
		detailLayout.addComponent(tfMovil);		

		TextField tfEmail = new TextField("Email");
		Utils.bind(fgEjecutivo, tfEmail,"email" );
		tfEmail.setNullRepresentation("");
		detailLayout.addComponent(tfEmail);

		HorizontalLayout botones = new HorizontalLayout();
		botones.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		botones.setSpacing(true);
		
		//agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		try {
        			fgEjecutivo.commit();
        			
        			Contacto contacto = fgEjecutivo.getItemDataSource().getBean();
	        		Cliente cliente = (Cliente) cbCliente.getValue();
	        		contacto.setCliente(cliente);
	        		service.saveContacto(contacto);
	        			        		
					//recarga las sucursales del cliente
					List<Contacto> ejecutivos = service.getEjecutivosByCliente(cliente);
					cbEjecutivo.getContainerDataSource().removeAllItems();
					((BeanItemContainer<Contacto>) cbEjecutivo.getContainerDataSource()).addAll(ejecutivos);
	        		
        			hsp.removeComponent(generalDetailLayout);	

            		//habilita los botones para agregar
    				enableButtons();
    				
        			Notification.show("Ejecutivo guardado correctamente",Type.TRAY_NOTIFICATION);
        			
        		}catch(CommitException ce) {
        			
        			Utils.validateEditor("el ejecutivo",ce);
        		}
        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        
        botones.addComponent(btnSave);
        botones.setComponentAlignment(btnSave, Alignment.BOTTOM_LEFT);
        
      //agrega un boton que cencela acción
        Button btnCancel = new Button("Cancelar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		fgEjecutivo.discard();
        		hsp.removeComponent(generalDetailLayout);	

        		//habilita los botones para agregar
				enableButtons();
        	}
        }){{
        	addStyleName("link");
        }};
        
        botones.addComponent(btnCancel);
        botones.setComponentAlignment(btnCancel, Alignment.BOTTOM_RIGHT);
        vl.addComponent(botones);
        
		return vl;
	}
	
	/*
	 * Permite dibujar el formulario de nuevo ingreso para ejecutivo
	 */
	private VerticalLayout drawFormAddSolicitante() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		
		final BeanFieldGroup<Solicitante> fgSolicitante = new BeanFieldGroup<Solicitante>(Solicitante.class);
		Solicitante solicitante = new Solicitante();
		solicitante.setCliente((Cliente) cbCliente.getValue());
		fgSolicitante.setItemDataSource(solicitante);
		
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
		TextField tfRut = new TextField("Rut");
		tfRut.setInputPrompt("11111111-1");
		tfRut.setNullRepresentation("");
		Utils.bind(fgSolicitante, tfRut,"rut" );
		detailLayout.addComponent(tfRut);

		// Loop through the properties, build fields for them and add the fields
        // to this UI 
		TextField tfNombre = new TextField("Nombre");
		tfNombre.setNullRepresentation("");
		Utils.bind(fgSolicitante, tfNombre,"nombres" );
		detailLayout.addComponent(tfNombre);
		
		TextField tfApellidoPaterno = new TextField("Apellido Paterno");
		Utils.bind(fgSolicitante, tfApellidoPaterno,"apellidoPaterno" );
		tfApellidoPaterno.setNullRepresentation("");
		detailLayout.addComponent(tfApellidoPaterno);
		
		TextField tfApellidoMaterno = new TextField("Apellido Materno");
		Utils.bind(fgSolicitante, tfApellidoMaterno,"apellidoMaterno" );
		tfApellidoMaterno.setNullRepresentation("");
		detailLayout.addComponent(tfApellidoMaterno);
		
		TextField tfFono = new TextField("Teléfono Fijo");
		Utils.bind(fgSolicitante, tfFono,"telefonoFijo" );
		tfFono.setNullRepresentation("");
		detailLayout.addComponent(tfFono);
		
		TextField tfMovil= new TextField("Teléfono Móvil");
		Utils.bind(fgSolicitante, tfMovil,"telefonoMovil" );
		tfMovil.setNullRepresentation("");
		detailLayout.addComponent(tfMovil);		

		TextField tfEmail = new TextField("Email");
		Utils.bind(fgSolicitante, tfEmail,"email" );
		tfEmail.setNullRepresentation("");
		detailLayout.addComponent(tfEmail);

		HorizontalLayout botones = new HorizontalLayout();
		botones.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		botones.setSpacing(true);
		
		//agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		try {
        			fgSolicitante.commit();
        			
        			Solicitante solicitante = fgSolicitante.getItemDataSource().getBean();
	        		Cliente cliente = (Cliente) cbCliente.getValue();
	        		solicitante.setCliente(cliente);
        			
	        		service.saveSolicitante(solicitante);
	        		
	        		//recarga las sucursales del cliente
					List<Solicitante> solicitantes = service.getSolicitantesByCliente(cliente);
					cbSolicitante.getContainerDataSource().removeAllItems();
					((BeanItemContainer<Solicitante>) cbSolicitante.getContainerDataSource()).addAll(solicitantes);
        			
        			hsp.removeComponent(generalDetailLayout);	

        			//habilita los botones para agregar
    				enableButtons();
    				
        			Notification.show("Solicitante guardado correctamente",Type.TRAY_NOTIFICATION);
            		
        		}catch(CommitException ce) {

        			Utils.validateEditor("el solicitante",ce);
        		}
        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        botones.addComponent(btnSave);
        botones.setComponentAlignment(btnSave, Alignment.BOTTOM_LEFT);
        
      //agrega un boton que cencela acción
        Button btnCancel = new Button("Cancelar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		
        		fgSolicitante.discard();
        		hsp.removeComponent(generalDetailLayout);	

        		//habilita los botones para agregar
				enableButtons();
        	}
        }){{
        	addStyleName("link");
        }};
        
        botones.addComponent(btnCancel);
        botones.setComponentAlignment(btnCancel, Alignment.BOTTOM_RIGHT);
        vl.addComponent(botones);
        
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
                
		
		cbCliente.setFilteringMode(FilteringMode.CONTAINS);
		cbCliente.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbCliente.setItemCaptionPropertyId("nombreCliente");
		BeanItemContainer<Cliente> ds = new BeanItemContainer<Cliente>(Cliente.class);
		List<Cliente> clientes = service.getClientes();
		ds.addAll(clientes);
		cbCliente.setContainerDataSource(ds);
		//lo define como requerido
		cbCliente.setRequired(true);
		cbCliente.setRequiredError("Es necesario seleccionar el cliente.");
		
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

								// deshabilita los botones
								disableButtons();
							}

						});				
						
				addComponents(cbCliente,btnAgregarCliente);
			}
		});
		
		Label clienteSel = new Label();
		gl.addComponent(clienteSel);
		
		// sucursal
		cbSucursal.setFilteringMode(FilteringMode.CONTAINS);
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

								// deshabilita los botones
								disableButtons();
							}
						});	
				btnSucursal.setEnabled(false);
				addComponents(cbSucursal,btnSucursal);
			}
		});
		Label sucursalSel = new Label();
		gl.addComponent(sucursalSel);
		
		cbEjecutivo.setFilteringMode(FilteringMode.CONTAINS);
		cbEjecutivo.setEnabled(false);
		cbEjecutivo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbEjecutivo.setItemCaptionPropertyId("nombreCompleto");
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

								// deshabilita los botones
								disableButtons();
							}
						});	
				btnEjecutivo.setEnabled(false);
				addComponents(cbEjecutivo,btnEjecutivo);
			}
		});
		Label ejecutivoSel = new Label();
		gl.addComponent(ejecutivoSel);
		
		
		cbSolicitante.setFilteringMode(FilteringMode.CONTAINS);
		cbSolicitante.setEnabled(false);
		cbSolicitante.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbSolicitante.setItemCaptionPropertyId("nombreCompleto");
		BeanItemContainer<Solicitante> ds4 = new BeanItemContainer<Solicitante>(Solicitante.class);
		cbSolicitante.setContainerDataSource(ds4);
		Utils.bind(fg,cbSolicitante, "solicitante");
		
		//solicitante
		gl.addComponents(new Label("Nombre Solicitante"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				btnSolicitante = new Button(FontAwesome.PLUS_CIRCLE);
				btnSolicitante.addClickListener(
						new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								generalDetailLayout = drawFormAddSolicitante();	
								hsp.addComponent(generalDetailLayout);
								// deshabilita los botones
								disableButtons();
							}
						});	
				btnSolicitante.setEnabled(false);
				addComponents(cbSolicitante,btnSolicitante);
			}
		});
		

		cbCliente.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				VaadinSession.getCurrent().setAttribute(Constants.SESSION_CLIENTE, null);
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
				
				VaadinSession.getCurrent().setAttribute(Constants.SESSION_CLIENTE, cliente);

				enableButtons();
			}
		});
		
		
		Label solicitanteSel = new Label();
		gl.addComponent(solicitanteSel);
		
		//solicitante
		gl.addComponents(new Label("Número Tasación Cliente:"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg, tf, "numeroTasacionCliente");
				addComponents(tf);
			}
		});
		Label solicitanteSel2 = new Label();
		gl.addComponent(solicitanteSel2);
		
		//tipo de operacion
		gl.addComponents(new Label("Tipo de Operación"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox cbTipoOperacion = new ComboBox();
				cbTipoOperacion.setItemCaptionMode(ItemCaptionMode.PROPERTY);
				cbTipoOperacion.setItemCaptionPropertyId("nombre");
				cbTipoOperacion.setContainerDataSource(new BeanItemContainer<TipoOperacion>(TipoOperacion.class));
				
				Utils.bind(fg,cbTipoOperacion, "tipoOperacion");
				List<TipoOperacion> operaciones = service.getOperaciones();
				int i = 0;
				for(TipoOperacion tipo : operaciones) {
					cbTipoOperacion.addItem(tipo);
					if(i == 0)
						cbTipoOperacion.setValue(tipo);
					i++;
				}
				addComponents(cbTipoOperacion);
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

	/* FUNCIONES DE APOYO */
	
	private void enableButtons() {
		endisButtons(true);
	}
	
	private void disableButtons() {
		endisButtons(false);
	}
	
	private void endisButtons(boolean enabled) {
		btnSucursal.setEnabled(enabled);
		btnAgregarCliente.setEnabled(enabled);
		btnEjecutivo.setEnabled(enabled);
		btnSolicitante.setEnabled(enabled);
	}


}
