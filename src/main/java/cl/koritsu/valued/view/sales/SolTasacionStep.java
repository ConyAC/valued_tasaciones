package cl.koritsu.valued.view.sales;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class SolTasacionStep implements WizardStep {

	@Override
	public String getCaption() {
		return "Sol. Tasación";
	}

	@Override
	public Component getContent() {
		VerticalLayout vl = new VerticalLayout();
		return vl;
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
