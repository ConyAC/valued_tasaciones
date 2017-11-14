package cl.koritsu.valued.view.sales;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class HonorarioClienteStep implements WizardStep {
	
	public HonorarioClienteStep(Wizard wizard){
		
	}

	@Override
	public String getCaption() {
		return "Ingresos Cliente";
	}

	@Override
	public Component getContent() {
		VerticalLayout vl = new VerticalLayout();
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
