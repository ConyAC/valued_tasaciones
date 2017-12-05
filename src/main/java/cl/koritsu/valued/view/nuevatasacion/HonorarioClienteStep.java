package cl.koritsu.valued.view.nuevatasacion;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.utils.Utils;

public class HonorarioClienteStep implements WizardStep {
	
	final String montoFijo = "Por monto fijo";
	final String montoKM = "Por KM de desplazamiento";
	BeanFieldGroup<SolicitudTasacion> fg;
	ValuedService service;
	
	VerticalLayout root = new VerticalLayout();
	
	public HonorarioClienteStep(Wizard wizard, BeanFieldGroup<SolicitudTasacion> fg,ValuedService service){
		this.fg = fg;
		this.service = service;
		
		initView();
	}

	private void initView() {

		root.setMargin(true);
		root.setSpacing(true);
		root.setWidth("100%");
		
		GridLayout glIngresoSolicitud = new GridLayout(2,2);
		glIngresoSolicitud.setWidth("100%");
		glIngresoSolicitud.setSpacing(true);
		
		glIngresoSolicitud.addComponents(new Label("Ingreso por Solicitud"));
		TextField ingresoCliente = new TextField();
		Utils.bind(fg, ingresoCliente, "honorarioCliente.montoHonorarioUF");
		glIngresoSolicitud.addComponents(ingresoCliente);
		
		CheckBox cb = new CheckBox("Tendrá desplazamiento?");
		glIngresoSolicitud.addComponent(cb,0,1,1,1);
		
		final GridLayout glMontoDesplazamientoFijo = new GridLayout(2,10);
		glMontoDesplazamientoFijo.setSpacing(true);
		glMontoDesplazamientoFijo.setWidth("100%");
		glMontoDesplazamientoFijo.setVisible(false);
		
		final OptionGroup og = new OptionGroup("Como se ingresa el monto");
		og.addItem(montoFijo);
		og.addItem(montoKM);
		og.select(montoFijo);
		og.setVisible(false);

		
		glMontoDesplazamientoFijo.addComponents(new Label("Ingreso monto por desplazamiento"));
		glMontoDesplazamientoFijo.addComponents(new TextField());
		
		final GridLayout glPorDesplazamiento = new GridLayout(2,8);
		glPorDesplazamiento.setWidth("100%");
		glPorDesplazamiento.setSpacing(true);
		glPorDesplazamiento.setVisible(false);
		
		//muestra el panel de calculo de desplazamiento sólo si se selecciona que si
		cb.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean isSelected = (Boolean)event.getProperty().getValue();
				og.setVisible(isSelected);
				if(isSelected) {
					String opcion= (String)og.getValue();
					mostrarOcultarPanel(opcion,glMontoDesplazamientoFijo,glPorDesplazamiento);
				}else {
					glMontoDesplazamientoFijo.setVisible(isSelected);
					glPorDesplazamiento.setVisible(isSelected);
				}
			}
		});
		
		og.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				String opcion= (String)event.getProperty().getValue();
				mostrarOcultarPanel(opcion,glMontoDesplazamientoFijo,glPorDesplazamiento);
			}
		});
		
		final Label lbFactorEmpresa = new Label("Factor UF/KM - Empresa");
		final Label lb2FactorEmpresa = new Label("1.1");
		
		CheckBox cb2 = new CheckBox("¿Utilizar otro Factor UF/KM?");
		glPorDesplazamiento.addComponent(cb2,0,0,1,0);
		
		glPorDesplazamiento.addComponents(lbFactorEmpresa);
		glPorDesplazamiento.addComponents(lb2FactorEmpresa);

		final Label lbFactor = new Label("Factor UF/KM");
		final TextField tfFactor = new TextField();
		
		glPorDesplazamiento.addComponents(lbFactor);
		glPorDesplazamiento.addComponents(tfFactor);
		
		cb2.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean isSelected = (Boolean)event.getProperty().getValue();
				lbFactorEmpresa.setVisible(!isSelected);
				lb2FactorEmpresa.setVisible(!isSelected);
				
				lbFactor.setVisible(isSelected);
				tfFactor.setVisible(isSelected);
				
			}
		});
		
		glPorDesplazamiento.addComponents(new Label("Km Total"));
		glPorDesplazamiento.addComponents(new TextField());
		
		glPorDesplazamiento.addComponents(new Label("Monto por\nDesplazamiento UF"));
		glPorDesplazamiento.addComponents(new TextField());
		
		glPorDesplazamiento.addComponents(new Label("Monto por\nDesplazamiento $"));
		glPorDesplazamiento.addComponents(new TextField(){{setEnabled(false);}});
		
		//return gl;
		root.addComponent(glIngresoSolicitud);
		root.addComponent(og);
		root.addComponent(glMontoDesplazamientoFijo);
		root.addComponent(glPorDesplazamiento);
	}

	@Override
	public String getCaption() {
		return "Ingresos Cliente";
	}

	@Override
	public Component getContent() {
		return root;
	}

	/**
	 * Muestra u oculta los paneles dados segùn el criterio de monto fijo o por km al tener asociado desplazamiento
	 * @param opcion
	 * @param glMontoDesplazamientoFijo
	 * @param glPorDesplazamiento
	 */
	protected void mostrarOcultarPanel(String opcion, GridLayout glMontoDesplazamientoFijo,
			GridLayout glPorDesplazamiento) {
		glMontoDesplazamientoFijo.setVisible(opcion.compareTo(montoFijo)==0);
		glPorDesplazamiento.setVisible(opcion.compareTo(montoKM)==0);
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
