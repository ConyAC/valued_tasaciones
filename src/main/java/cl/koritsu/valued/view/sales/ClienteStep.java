package cl.koritsu.valued.view.sales;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ClienteStep implements WizardStep {

	@Override
	public String getCaption() {
		return "Cliente";
	}

	@Override
	public Component getContent() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		
		vl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(new Label("Nombre Cliente"),tf,btn);
			}
		});
		
		vl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(new Label("Nombre Sucursal"),tf,btn);
			}
		});
		
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
