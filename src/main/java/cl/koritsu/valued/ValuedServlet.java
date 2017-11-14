package cl.koritsu.valued;

import javax.servlet.ServletException;

import com.vaadin.server.VaadinServlet;

@SuppressWarnings("serial")
public class ValuedServlet extends VaadinServlet {

    @Override
    protected final void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new ValuedSessionInitListener());
    }
}