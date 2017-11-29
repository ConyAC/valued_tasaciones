package cl.koritsu.valued;

import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class ValuedServlet extends ru.xpoft.vaadin.SpringVaadinServlet {

    @Override
    protected final void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new ValuedSessionInitListener());
    }
}