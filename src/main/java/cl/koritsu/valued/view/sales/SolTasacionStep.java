package cl.koritsu.valued.view.sales;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import cl.koritsu.valued.domain.enums.TIPO_INFORME;

public class SolTasacionStep implements WizardStep {

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
				for(TIPO_INFORME tipo : TIPO_INFORME.values()) {
					tf.addItem(tipo);
				}
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
				cb.addItem("$");
				cb.addItem("UF");
				TextField tf = new TextField();
				addComponents(cb,tf);
			}
		});
		
		gl.addComponent(new Label(""));
		
		//sucursal
		gl.addComponents(new Label("Requiere tasador"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				CheckBox tf = new CheckBox();
				addComponents(tf);
			}
		});
		gl.addComponent(new Label(""));
		
		//ejecutivo
		gl.addComponents(new Label("Nombre Tasador"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(tf,btn);
			}
		});
		gl.addComponent(new Label(""));
		
		//solicitante
		gl.addComponents(new Label("Fecha Encargo"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				DateField tf = new DateField();
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

