package cl.koritsu.valued.view.nuevatasacion;

import java.util.List;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

import cl.koritsu.valued.component.ClienteWindow;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Region;
import cl.koritsu.valued.domain.TipoOperacion;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.nuevatasacion.vo.NuevaSolicitudVO;
import cl.koritsu.valued.view.utils.Utils;


public class ClienteStep implements WizardStep {
	
	FormLayout testLayout, form;
	VerticalLayout generalDetailLayout, ejecutivoDetailLayout;
	HorizontalSplitPanel hsp;
	Button btnEjecutivo, btnSucursal;
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
	
    private Cliente getCurrentCliente() {
        return (Cliente) VaadinSession.getCurrent().getAttribute(
        		Cliente.class.getName());
    }

	@Override
	public Component getContent() {
		
		hsp = new HorizontalSplitPanel();
		hsp.setSizeFull();
		
		VerticalLayout usersListLayout = drawForm();
		hsp.addComponent(usersListLayout);
				
		return hsp;
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

		// Loop through the properties, build fields for them and add the fields
        // to this UI 
		TextField tfNombre = new TextField("Nombre");
		tfNombre.setNullRepresentation("");
		detailLayout.addComponent(tfNombre);
		
		TextField tfDireccion = new TextField("Dirección");
		tfDireccion.setNullRepresentation("");
		detailLayout.addComponent(tfDireccion);
		
		ComboBox cbComuna = new ComboBox("Comuna");
		cbComuna.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbComuna.setItemCaptionPropertyId("name");
		cbComuna.setWidth("100%");
		detailLayout.addComponent(cbComuna);
		
		ComboBox cbRegion = new ComboBox("Región");
		cbRegion.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbRegion.setItemCaptionPropertyId("name");
		cbRegion.setWidth("100%");
		detailLayout.addComponent(cbRegion);

		HorizontalLayout botones = new HorizontalLayout();
		botones.setHeight("60px");
		botones.setSpacing(true);
		
		//agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		Notification.show("Sucursal guardada correctamente",Type.TRAY_NOTIFICATION);
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
                
		// cliente
		gl.addComponents(new Label("Nombre Cliente"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf, "cliente");
				final Cliente cliente = getCurrentCliente();
				Button btnAgregarCliente = new Button(null,FontAwesome.PLUS_CIRCLE);				  
				btnAgregarCliente.addClickListener(
						new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								ClienteWindow.open(cliente);								
							}
						});				
						
				addComponents(tf,btnAgregarCliente);
			}
		});
		
		Label clienteSel = new Label();
		gl.addComponent(clienteSel);
		
		//sucursal
		gl.addComponents(new Label("Nombre Sucursal"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf, "sucursal");
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
			        
				addComponents(tf,btnSucursal);
			}
		});
		Label sucursalSel = new Label();
		gl.addComponent(sucursalSel);
		
		
		//ejecutivo
		gl.addComponents(new Label("Nombre Ejecutivo"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf, "ejecutivo");
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
				addComponents(tf,btnEjecutivo);
			}
		});
		Label ejecutivoSel = new Label();
		gl.addComponent(ejecutivoSel);
		
		//solicitante
		gl.addComponents(new Label("Nombre Solicitante"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Utils.bind(fg,tf, "solicitante");
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(tf,btn);
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
