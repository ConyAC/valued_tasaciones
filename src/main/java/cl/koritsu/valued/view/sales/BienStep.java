package cl.koritsu.valued.view.sales;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import cl.koritsu.valued.domain.enums.TIPO_BIEN;

public class BienStep implements WizardStep {

	@Override
	public String getCaption() {
		return "Bien";
	}

	@Override
	public Component getContent() {
		GridLayout gl = new GridLayout(4,20);
		gl.setSpacing(true);
		gl.setMargin(true);
		gl.setWidth("100%");
		
		
		// clase de bien
		gl.addComponents(new Label("Clase Bien"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				OptionGroup tf = new OptionGroup();
				tf.addItem("Inmueble");
				tf.addItem("Mueble");
				addComponents(tf);
			}
		});
		gl.addComponent(new Label("Mapa"),2,0,3,6);
		
		//tipo de bien
		gl.addComponents(new Label("Tipo de Bien"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				for(TIPO_BIEN tipo : TIPO_BIEN.values()) {
					tf.addItem(tipo);
				}
				addComponents(tf);
			}
		});
		
		//region
		gl.addComponents(new Label("Región"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				for(String tipo : new String[] {"",""}) {
					tf.addItem(tipo);
				}
				addComponents(tf);
			}
		});
		
		//comuna
		gl.addComponents(new Label("Comuna"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				for(String tipo : new String[] {"",""}) {
					tf.addItem(tipo);
				}
				addComponents(tf);
			}
		});
		
		//calle
		gl.addComponents(new Label("Calle"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//solicitante
		gl.addComponents(new Label("Número"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//solicitante
		gl.addComponents(new Label("Unidad"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		// propietario
		gl.addComponent(new Label("Información sobre propietario"),0,7,3,7);
		
		//rut
		gl.addComponents(new Label("RUT"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//telefono
		gl.addComponents(new Label("Teléfono"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//nombre
		gl.addComponents(new Label("Nombre"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//correo
		gl.addComponents(new Label("Correo"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		// contacto
		gl.addComponent(new Label("Información sobre contacto"),0,10,3,10);
		
		//rut
		gl.addComponents(new Label("RUT"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//telefono
		gl.addComponents(new Label("Teléfono"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//nombre
		gl.addComponents(new Label("Nombre"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
		//correo
		gl.addComponents(new Label("Correo"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				addComponents(tf);
			}
		});
		
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
