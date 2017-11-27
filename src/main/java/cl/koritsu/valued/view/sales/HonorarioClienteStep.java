package cl.koritsu.valued.view.sales;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class HonorarioClienteStep implements WizardStep {
	
	final String montoFijo = "Por monto fijo";
	final String montoKM = "Por KM de desplazamiento";
	
	public HonorarioClienteStep(Wizard wizard){
		
	}

	@Override
	public String getCaption() {
		return "Ingresos Cliente";
	}

	@Override
	public Component getContent() {
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setWidth("100%");
		
		GridLayout glIngresoSolicitud = new GridLayout(2,2);
		glIngresoSolicitud.setWidth("100%");
		glIngresoSolicitud.setSpacing(true);
		
		glIngresoSolicitud.addComponents(new Label("Ingreso por Solicitud(UF)"));
		glIngresoSolicitud.addComponents(new TextField());
		
		CheckBox cb = new CheckBox("Tendrá desplazamiento?");
		glIngresoSolicitud.addComponent(cb,0,1,1,1);
		
		final GridLayout glTipoMontoDesplazamiento = new GridLayout(2,10);
		glTipoMontoDesplazamiento.setSpacing(true);
		glTipoMontoDesplazamiento.setWidth("100%");
		
		//muestra el panel de calculo de desplazamiento sólo si se selecciona que si
		cb.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean isSelected = (Boolean)event.getProperty().getValue();
				glTipoMontoDesplazamiento.setVisible(isSelected);
			}
		});
		
		OptionGroup og = new OptionGroup("Como se ingresa el monto");
		og.addItem(montoFijo);
		og.addItem(montoKM);
		og.select(montoFijo);
		
		glTipoMontoDesplazamiento.addComponent(og,0,2,1,2);
		
		glTipoMontoDesplazamiento.addComponents(new Label("Ingreso monto por desplazamiento"));
		glTipoMontoDesplazamiento.addComponents(new TextField());
		
		GridLayout gl = new GridLayout(2,8);
		glIngresoSolicitud.setWidth("100%");
		glIngresoSolicitud.setSpacing(true);
		
		gl.addComponents(new Label("Factor UF/KM"));
		gl.addComponents(new Label("1.1"));
		
		CheckBox cb2 = new CheckBox("¿Utilizar otro Factor UF/Km?");
		gl.addComponent(cb2,0,5,1,5);
		
		gl.addComponents(new Label("Factor UF/KM"));
		gl.addComponents(new TextField());
		
		gl.addComponents(new Label("Km Total"));
		gl.addComponents(new TextField());
		
		gl.addComponents(new Label("Monto por\nDesplazamiento UF"));
		gl.addComponents(new TextField());
		
		gl.addComponents(new Label("Monto por\nDesplazamiento $"));
		gl.addComponents(new TextField(){{setEnabled(false);}});
		
		//return gl;
		vl.addComponent(glIngresoSolicitud);
		vl.addComponent(gl);
		
		return vl;
	}

	@Override
	public boolean onAdvance() {
		return false;
	}

	@Override
	public boolean onBack() {
		return true;
	}

}
