package cl.koritsu.valued.view.sales;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import cl.koritsu.valued.component.ClienteWindow;
import cl.koritsu.valued.domain.Cliente;

import com.vaadin.ui.TextField;

import cl.koritsu.valued.domain.enums.TIPO_OPERACION;

public class ClienteStep implements WizardStep {

	@Override
	public String getCaption() {
		return "Cliente";
	}
	
    private Cliente getCurrentCliente() {
        return (Cliente) VaadinSession.getCurrent().getAttribute(
        		Cliente.class.getName());
    }

	@Override
	public Component getContent() {
		GridLayout gl = new GridLayout(3,10);
		gl.setSpacing(true);
		gl.setMargin(true);
		gl.setWidth("100%");
		
		
		// cliente
		gl.addComponents(new Label("Nombre Cliente"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				
				final Cliente cliente = getCurrentCliente();
				Button btnAgregarCliente = new Button(null,FontAwesome.PLUS_CIRCLE);				  
				btnAgregarCliente.addClickListener(
						new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								// TODO Auto-generated method stub
								ClienteWindow.open(cliente, false);								
							}
						});				
						
				addComponents(tf,btnAgregarCliente);
			}
		});
		
		Label clienteSel = new Label();
		gl.addComponent(clienteSel);
		
		//sucursal
		gl.addComponents(new Label("Nombre Sucursal"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(tf,btn);
			}
		});
		Label sucursalSel = new Label();
		gl.addComponent(sucursalSel);
		
		
		//ejecutivo
		gl.addComponents(new Label("Nombre Ejecutivo"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(tf,btn);
			}
		});
		Label ejecutivoSel = new Label();
		gl.addComponent(ejecutivoSel);
		
		//solicitante
		gl.addComponents(new Label("Nombre Solicitante"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				TextField tf = new TextField();
				Button btn = new Button(FontAwesome.PLUS_CIRCLE);
				addComponents(tf,btn);
			}
		});
		Label solicitanteSel = new Label();
		gl.addComponent(solicitanteSel);
		
		//tipo de operacion
		gl.addComponents(new Label("Tipo de Operación"));
		gl.addComponent(new HorizontalLayout(){
			{
				setSpacing(true);
				ComboBox tf = new ComboBox();
				for(TIPO_OPERACION tipo : TIPO_OPERACION.values()) {
					tf.addItem(tipo);
				}
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
		return false;
	}

}
