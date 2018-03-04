package cl.koritsu.valued.view.nuevatasacion;

import java.util.Date;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.utils.Constants;
import cl.koritsu.valued.view.utils.Utils;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class HonorarioClienteStep implements WizardStep {
	
	final String montoFijo = "Por monto fijo";
	final String montoKM = "Por KM de desplazamiento";
	final Label lb2FactorEmpresa = new Label();
	final TextField tfDesplazamientoUF = new TextField();
	final TextField tfDesplazamientoPesos = new TextField();
	final TextField tfKMTotal = new TextField();
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
		glIngresoSolicitud.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				final ComboBox cb = new ComboBox();
				cb.setWidth("70px");
				cb.addItem("$");
				cb.addItem("UF");
				cb.setValue("$");
				final TextField tf = new TextField();
				addComponents(cb,tf);
				Utils.bind(fg, tf, "honorarioCliente.montoHonorarioPesos");
				cb.addValueChangeListener(new ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						if(cb.getValue().equals("$"))
							Utils.bind(fg, tf, "honorarioCliente.montoHonorarioPesos");
						else
							Utils.bind(fg, tf, "honorarioCliente.montoHonorarioUF");
					}
				});
				
				setExpandRatio(tf, 1f);
			}
		});
		
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
				Cliente cliente  = (Cliente) VaadinSession.getCurrent().getAttribute(Constants.SESSION_CLIENTE);
				if(cliente != null && cliente.getFactorKm() != null)
					lb2FactorEmpresa.setValue(cliente.getFactorKm());

				String opcion= (String)event.getProperty().getValue();
				mostrarOcultarPanel(opcion,glMontoDesplazamientoFijo,glPorDesplazamiento);
			}
		});
		
		final Label lbFactorEmpresa = new Label("Factor UF/KM - Empresa");
		final Label lbFactor = new Label("Factor UF/KM");
		final TextField tfFactor = new TextField();
		
		CheckBox cb2 = new CheckBox("¿Utilizar otro Factor UF/KM?");
		glPorDesplazamiento.addComponent(cb2,0,0,1,0);
		
		glPorDesplazamiento.addComponents(lbFactorEmpresa);
		glPorDesplazamiento.addComponents(lb2FactorEmpresa);
		
		glPorDesplazamiento.addComponents(lbFactor);
		glPorDesplazamiento.addComponents(tfFactor);
		Utils.bind(fg, tfFactor, "honorarioCliente.factorKmUf");
		
		cb2.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				tfFactor.setValue("0");
				tfKMTotal.setValue("0");
				tfDesplazamientoPesos.setValue("0");
				tfDesplazamientoUF.setValue("0");
				
				Boolean isSelected = (Boolean)event.getProperty().getValue();
				lbFactorEmpresa.setVisible(!isSelected);
				lb2FactorEmpresa.setVisible(!isSelected);
				
				lbFactor.setVisible(isSelected);
				tfFactor.setVisible(isSelected);
				
			}
		});
		
		glPorDesplazamiento.addComponents(new Label("Km Total"));
		glPorDesplazamiento.addComponents(tfKMTotal);
		Utils.bind(fg, tfKMTotal, "honorarioCliente.kmTotal");
		
		
		glPorDesplazamiento.addComponents(new Label("Monto por\nDesplazamiento UF"));
		glPorDesplazamiento.addComponents(tfDesplazamientoUF);
		Utils.bind(fg, tfDesplazamientoUF, "honorarioCliente.montoHonorarioDesplazamientoUF");
		
		
		glPorDesplazamiento.addComponents(new Label("Monto por\nDesplazamiento $"));
		tfDesplazamientoPesos.setEnabled(false);
		glPorDesplazamiento.addComponents(tfDesplazamientoPesos);
		Utils.bind(fg, tfDesplazamientoPesos, "honorarioCliente.montoHonorarioDesplazamientoPesos");
		
		tfKMTotal.addValueChangeListener(new ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	if(tfKMTotal.getValue() != null && lb2FactorEmpresa.getValue() != null){
		    		float desplazamiento = Float.parseFloat(tfKMTotal.getValue().toString()) * Float.parseFloat(lb2FactorEmpresa.getValue().toString());
					tfDesplazamientoUF.setValue(String.valueOf(desplazamiento));
				}
		    	
				Double valorUF = service.getValorUFporFecha(new Date());
		    	if(valorUF != null && tfDesplazamientoUF.getValue() != null){
		    		float pesos = valorUF.floatValue() * Float.parseFloat(tfDesplazamientoUF.getValue().toString());		
					tfDesplazamientoPesos.setValue(String.valueOf(pesos));
				}
		    }
		});
		
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
