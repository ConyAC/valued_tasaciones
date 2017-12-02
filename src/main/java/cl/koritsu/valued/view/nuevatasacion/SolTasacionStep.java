package cl.koritsu.valued.view.nuevatasacion;

import java.util.Date;
import java.util.List;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.TipoInforme;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.utils.Utils;

public class SolTasacionStep implements WizardStep {
	
	BeanFieldGroup<SolicitudTasacion> fg;
	ValuedService service;

	public SolTasacionStep(BeanFieldGroup<SolicitudTasacion> fg,ValuedService service) {
		this.fg = fg;
		this.service = service;
	}

	@Override
	public String getCaption() {
		return "Sol. Tasación";
	}

	@Override
	public Component getContent() {
		GridLayout gl = new GridLayout(3,10);
		gl.setSpacing(true);
		gl.setMargin(true);
		gl.setWidth("100%");
		
		//tipo de operacion
		gl.addComponents(new Label("Tipo de Informe"));
		gl.addComponent(new HorizontalLayout(){
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
		gl.addComponent(new Label(""));
		
		// cliente
		gl.addComponents(new Label("Valor Compra o estimado"));
		gl.addComponent(new HorizontalLayout(){
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
		
		gl.addComponent(new Label(""));
		
		//sucursal
		gl.addComponents(new Label("Requiere tasador"));
		
		final CheckBox cb = new CheckBox();
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				cb.setValue(true);
				addComponents(cb);
			}
		});
		gl.addComponent(new Label(""));
		
		//ejecutivo
		final Label lbNombreTasador = new Label("Nombre Tasador");
		final HorizontalLayout hlNombreTasador =  new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				tf.setItemCaptionMode(ItemCaptionMode.PROPERTY);
				tf.setItemCaptionPropertyId("nombres");
				Utils.bind(fg, tf, "tasador");
				BeanItemContainer<Usuario> ds = new BeanItemContainer<Usuario>(Usuario.class);
				tf.setContainerDataSource(ds);
				
				List<Usuario> usuarios = service.getTasadores();
				ds.addAll(usuarios);
				
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(tf,btn);
			}
		};
		gl.addComponents(lbNombreTasador);
		gl.addComponent(hlNombreTasador);
		gl.addComponent(new Label(""));
		
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
		gl.addComponents(new Label("Fecha Encargo"));
		gl.addComponent(new HorizontalLayout(){
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
		gl.addComponent(solicitanteSel);

		
		return gl;
	}

	@Override
	public boolean onAdvance() {
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}

}

