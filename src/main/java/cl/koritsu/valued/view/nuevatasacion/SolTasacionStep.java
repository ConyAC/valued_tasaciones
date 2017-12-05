package cl.koritsu.valued.view.nuevatasacion;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.TipoInforme;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.utils.Utils;

public class SolTasacionStep implements WizardStep {
	
	BeanFieldGroup<SolicitudTasacion> fg;
	ValuedService service;
	HorizontalSplitPanel hsp;
	VerticalLayout generalDetailLayout;
	ComboBox cbTasador = new ComboBox();
	Button btnAgregarTasador;
	
	String MASCULINO = "Masculino";
	String FEMENINO = "Femenino";

	public SolTasacionStep(BeanFieldGroup<SolicitudTasacion> fg,ValuedService service) {
		this.fg = fg;
		this.service = service;
		initView();
	}

	private void initView() {
		
		hsp = new HorizontalSplitPanel();
		hsp.setSizeFull();
		
		GridLayout glRoot = new GridLayout(3,10);
		hsp.addComponent(glRoot);

		glRoot.setSpacing(true);
		glRoot.setMargin(true);
		glRoot.setWidth("100%");
		
		//tipo de operacion
		glRoot.addComponents(new Label("Tipo de Informe"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				tf.setItemCaptionMode(ItemCaptionMode.PROPERTY);
				tf.setItemCaptionPropertyId("nombre");
				BeanItemContainer<TipoInforme> tipoInformeDS = new BeanItemContainer<TipoInforme>(TipoInforme.class);
				tf.setContainerDataSource(tipoInformeDS);
				
				Utils.bind(fg, tf, "tipoInforme");
				
				List<TipoInforme> informes = service.getTipoInformes();
				tipoInformeDS.addAll(informes);
				
				addComponents(tf);
			}
		});
		glRoot.addComponent(new Label(""));
		
		// cliente
		glRoot.addComponents(new Label("Valor Compra o estimado"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox cb = new ComboBox();
				cb.setWidth("70px");
				cb.addItem("$");
				cb.addItem("UF");
				cb.setValue("$");
				TextField tf = new TextField();
				addComponents(cb,tf);
				setExpandRatio(tf, 1f);
			}
		});
		
		glRoot.addComponent(new Label(""));
		
		//sucursal
		glRoot.addComponents(new Label("Requiere tasador"));
		
		final CheckBox cb = new CheckBox();
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				cb.setValue(true);
				addComponents(cb);
			}
		});
		glRoot.addComponent(new Label(""));
		
		//ejecutivo
		final Label lbNombreTasador = new Label("Nombre Tasador");
		final HorizontalLayout hlNombreTasador =  new HorizontalLayout(){
			{
				setSpacing(true);
				cbTasador.setItemCaptionMode(ItemCaptionMode.PROPERTY);
				cbTasador.setItemCaptionPropertyId("nombres");
				Utils.bind(fg, cbTasador, "tasador");
				BeanItemContainer<Usuario> ds = new BeanItemContainer<Usuario>(Usuario.class);
				cbTasador.setContainerDataSource(ds);
				
				List<Usuario> usuarios = service.getTasadores();
				ds.addAll(usuarios);
				
				btnAgregarTasador = new Button(FontAwesome.PLUS_CIRCLE);
				btnAgregarTasador.addClickListener(new ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						generalDetailLayout = drawFormAddTasador();
						hsp.addComponent(generalDetailLayout);
						
						btnAgregarTasador.setEnabled(false);
					}
				});
				addComponents(cbTasador,btnAgregarTasador);
			}
		};
		glRoot.addComponents(lbNombreTasador);
		glRoot.addComponent(hlNombreTasador);
		glRoot.addComponent(new Label(""));
		
		//solo muestra el tasador si el checkbox está seleccionado
		cb.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean isSelected = (Boolean)event.getProperty().getValue();
				lbNombreTasador.setVisible(isSelected);
				hlNombreTasador.setVisible(isSelected);
			}
		});
		
		//solicitante
		glRoot.addComponents(new Label("Fecha Encargo"));
		glRoot.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				DateField tf = new DateField();
				tf.setDateFormat("dd/MM/yyyy");
				Utils.bind(fg, tf, "fechaEncargo");
				//setea la fecha de hoy
				tf.setValue(new Date());
				addComponents(tf);
			}
		});
		Label solicitanteSel = new Label();
		glRoot.addComponent(solicitanteSel);
	}

	
	
	/*
	 * Permite dibujar el formulario de nuevo ingreso para sucursal
	 */
	private VerticalLayout drawFormAddTasador() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		
		FormLayout detailLayout = new FormLayout();
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		
		Panel p = new Panel(detailLayout);
		p.setCaption("Creando nuevo Tasador");
		p.setSizeFull();
		vl.addComponent(p);
		vl.setExpandRatio(p, 1.0f);
		
		final BeanFieldGroup<Usuario> fgUsuario = new BeanFieldGroup<Usuario>(Usuario.class);
		Usuario usuario = new Usuario();
		usuario.setTasador(true);
		fgUsuario.setItemDataSource(usuario);

		// Loop through the properties, build fields for them and add the fields
        // to this UI 
		TextField tfNombre = new TextField("Nombre");
		tfNombre.setWidth("100%");tfNombre.setRequired(true);tfNombre.setRequiredError("El nombre es requerido");
		Utils.bind(fgUsuario, tfNombre, "nombres");
		tfNombre.setNullRepresentation("");
		detailLayout.addComponent(tfNombre);
		
		TextField tfApellidoPaterno = new TextField("Apellido Paterno");
		tfApellidoPaterno.setWidth("100%");
		Utils.bind(fgUsuario, tfApellidoPaterno, "apellidoPaterno");
		tfApellidoPaterno.setNullRepresentation("");
		detailLayout.addComponent(tfApellidoPaterno);
		
		TextField tfApellidoMaterno = new TextField("Apellido Materno");
		tfApellidoMaterno.setWidth("100%");
		Utils.bind(fgUsuario, tfApellidoMaterno, "apellidoMaterno");
		tfApellidoMaterno.setNullRepresentation("");
		detailLayout.addComponent(tfApellidoMaterno);
		
		OptionGroup tfGenero = new OptionGroup("Genero");
		tfGenero.setConverter(new Converter() {

			@Override
			public Object convertToModel(Object value, Class targetType, Locale locale) throws ConversionException {
				if(value == null) return false;
				if(((String) value).compareTo(MASCULINO) == 0 ) return true ;
				else return false;
			}

			@Override
			public Object convertToPresentation(Object value, Class targetType, Locale locale)
					throws ConversionException {
				if(value == null) return MASCULINO;
				if((Boolean) value) return MASCULINO;
				else return FEMENINO;
			}

			@Override
			public Class getModelType() {
				return Boolean.class;
			}

			@Override
			public Class getPresentationType() {
				return String.class;
			}
		});

		tfGenero.addItem(MASCULINO);
		tfGenero.addItem(FEMENINO);
		
		tfGenero.setWidth("100%");
		Utils.bind(fgUsuario, tfGenero, "male");
		detailLayout.addComponent(tfGenero);

		TextField tfEmail = new TextField("Email");
		tfEmail.setWidth("100%");tfEmail.setRequired(true);tfEmail.setRequiredError("El correo es requerido");
		Utils.bind(fgUsuario, tfEmail, "email");
		tfEmail.setNullRepresentation("");
		detailLayout.addComponent(tfEmail);
		
		PasswordField tfContrasena = new PasswordField("Contraseña");
		tfContrasena.setWidth("100%");tfContrasena.setRequired(true);tfContrasena.setRequiredError("La contraseña es requerida");
		Utils.bind(fgUsuario, tfContrasena, "contrasena");
		tfContrasena.setNullRepresentation("");
		detailLayout.addComponent(tfContrasena);

		HorizontalLayout botones = new HorizontalLayout();
		botones.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		botones.setSpacing(true);
		
		//agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {

        		try {
        			
					fgUsuario.commit();
					//setea
					Usuario usuario = fgUsuario.getItemDataSource().getBean();
	        		service.saveUsuario(usuario);
	        		
					//recarga las sucursales del cliente
					List<Usuario> tasadores  = service.getTasadores();
					cbTasador.getContainerDataSource().removeAllItems();
					((BeanItemContainer<Usuario>) cbTasador.getContainerDataSource()).addAll(tasadores);
					
	        		
	        		hsp.removeComponent(generalDetailLayout);	
	        		
	        		btnAgregarTasador.setEnabled(true);
					
					Notification.show("Tasador guardado correctamente",Type.TRAY_NOTIFICATION);
				} catch (CommitException e) {

					validateEditor("el tasador",e);
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
        		fgUsuario.discard();
        		hsp.removeComponent(generalDetailLayout);
        		btnAgregarTasador.setEnabled(true);
        	}
        }){{
        	addStyleName("link");
        }};
        
        botones.addComponent(btnCancel);
        botones.setComponentAlignment(btnCancel, Alignment.BOTTOM_RIGHT);
        vl.addComponent(botones);
        
		return vl;
	}
	
	@Override
	public String getCaption() {
		return "Sol. Tasación";
	}

	@Override
	public Component getContent() {
		return hsp;
	}

	@Override
	public boolean onAdvance() {
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}

	private void validateEditor(String msj,CommitException e) {
		e.printStackTrace();
		Map<Field<?>, Validator.InvalidValueException> invalidFields = e.getInvalidFields();
		if(!invalidFields.isEmpty()) {
			for(Entry<Field<?>, InvalidValueException> entry : invalidFields.entrySet() ) {
				Notification.show("Error al guardar "+msj+" debido a : \""+entry.getValue().getMessage()+"\"",Type.ERROR_MESSAGE);
				entry.getKey().focus();
				return;
			}
		}else {
			Notification.show("Error al guardar "+msj+" debido a "+e.getMessage(),Type.ERROR_MESSAGE);
		}
	}	
}

