package cl.koritsu.valued.view.facturacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import ru.xpoft.vaadin.VaadinView;
import cl.koritsu.valued.view.transactions.MisSolicitudesView;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = FacturacionView.NAME, cached = true)
public class FacturacionView extends VerticalLayout implements View {
	
Logger logger = LoggerFactory.getLogger(MisSolicitudesView.class);
	
	public static final String NAME = "facturar";


	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	
}
