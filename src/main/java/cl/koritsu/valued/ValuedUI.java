package cl.koritsu.valued;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.data.DataProvider;
import cl.koritsu.valued.data.dummy.DummyDataProvider;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.event.ValuedEvent.BrowserResizeEvent;
import cl.koritsu.valued.event.ValuedEvent.CloseOpenWindowsEvent;
import cl.koritsu.valued.event.ValuedEvent.UserLoggedOutEvent;
import cl.koritsu.valued.event.ValuedEvent.UserLoginRequestedEvent;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.view.LoginView;
import cl.koritsu.valued.view.MainView;
import cl.koritsu.valued.view.MainView2;
import cl.koritsu.valued.view.dashboard.DashboardView;
import cl.koritsu.valued.view.schedule.AdministrationView;
import cl.koritsu.valued.view.utils.SecurityHelper;
import cl.koritsu.valued.view.utils.Constants;

@org.springframework.stereotype.Component
@Scope("prototype")
@Theme("dashboard")
@Widgetset("cl.koritsu.valued.ValuedWidgetSet")
@Title("Valued")
@SuppressWarnings("serial")
public final class ValuedUI extends UI implements ErrorHandler {
	
	private transient static Logger logger = LoggerFactory.getLogger(ValuedUI.class);

    /*
     * This field stores an access to the dummy backend layer. In real
     * applications you most likely gain access to your beans trough lookup or
     * injection; and not in the UI but somewhere closer to where they're
     * actually accessed.
     */
    private final DataProvider dataProvider = new DummyDataProvider();
    private final ValuedEventBus dashboardEventbus = new ValuedEventBus();
    
    @Override
    protected void init(final VaadinRequest request) {
        setLocale(Locale.GERMAN);
        
        ValuedEventBus.register(this);
        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);

        updateContent();

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event bus on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                new BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            final BrowserWindowResizeEvent event) {
                        ValuedEventBus.post(new BrowserResizeEvent());
                    }
                });
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateContent() {
        Usuario user = (Usuario) VaadinSession.getCurrent().getAttribute(
                Usuario.class.getName());
        if (user != null && "admin".equals("admin")) {
        	
        	
        	if(SecurityHelper.isLogged()){
        		System.out.println("logeado ");
				if(menuItems != null)
					// verifica los permisos para mostrar los menus
					for(MenuItem menu : menuItems){
						menu.setVisible(SecurityHelper.hasMenu(menu.getText()));
					}
				//si no está logeado y la siguiente vista no es el login, lo redirige
			}
        	
        	
            // Authenticated user
            setContent(new MainView());
            removeStyleName("loginview");
            String state = getNavigator().getState();
            if(state != null && state.trim().length() == 0 )
            	getNavigator().navigateTo(DashboardView.NAME);
            else
            	getNavigator().navigateTo(getNavigator().getState());
        } else {
        	setContent(new MainView2());
        	getNavigator().navigateTo(LoginView.NAME);
            addStyleName("loginview");
        }
     
    }

    @Subscribe
    public void userLoginRequested(final UserLoginRequestedEvent event) {
        Usuario user = (Usuario) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO);
        VaadinSession.getCurrent().setAttribute(Usuario.class.getName(), user);
        updateContent();
    }

    @Subscribe
    public void userLoggedOut(final UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().setAttribute(Constants.SESSION_USUARIO, null);
        VaadinSession.getCurrent().setAttribute(Constants.SESSION_CLIENTE, null);
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @Subscribe
    public void closeOpenWindows(final CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
        	if(!window.getData().equals("no_cerrar") || !getNavigator().getState().equals("en_proceso"))
        		window.setVisible(false);
        	else
        		window.setVisible(true);
        }
    }

    /**
     * @return An instance for accessing the (dummy) services layer.
     */
    public static DataProvider getDataProvider() {
        return ((ValuedUI) getCurrent()).dataProvider;
    }

    public static ValuedEventBus getDashboardEventbus() {
        return ((ValuedUI) getCurrent()).dashboardEventbus;
    }

    @Override
	public void error(com.vaadin.server.ErrorEvent event) {
		// Finds the original source of the error/exception
		AbstractComponent component = DefaultErrorHandler.findAbstractComponent(event);
		if (component != null) {
			ErrorMessage errorMessage = getErrorMessageForException(event.getThrowable());
			if (errorMessage != null) {
				component.setComponentError(errorMessage);
				new Notification(null, errorMessage.getFormattedHtmlMessage(), Type.WARNING_MESSAGE, true).show(Page.getCurrent());
				return;
			}
		}
		logger.error("Error sin abstractComponent",event.getThrowable());
		DefaultErrorHandler.doDefault(event);
	}
    
	public static ErrorMessage getErrorMessageForException(Throwable t) {

		ConstraintViolationException validationException = getCauseOfType(t, ConstraintViolationException.class);
		if (validationException != null) {
			for(ConstraintViolation<?> c : validationException.getConstraintViolations())
				logger.error("Error al validar {}.{} = {} \n{}",c.getRootBeanClass(),c.getPropertyPath(),c.getInvalidValue(),c.getMessage());
			return new UserError(validationException.getMessage(),AbstractErrorMessage.ContentMode.TEXT, ErrorMessage.ErrorLevel.ERROR);
		}
		PersistenceException persistenceException = getCauseOfType(t, PersistenceException.class);
		if (persistenceException != null) {
			logger.error("Error al persistir.",persistenceException);
			return new UserError(persistenceException.getLocalizedMessage(), AbstractErrorMessage.ContentMode.TEXT, ErrorMessage.ErrorLevel.ERROR);
		}
		FieldGroup.CommitException commitException = getCauseOfType(t, FieldGroup.CommitException.class);
		if (commitException != null) {
			logger.error("Error al comitear.",commitException);
			return new UserError(commitException.getMessage(),AbstractErrorMessage.ContentMode.TEXT, ErrorMessage.ErrorLevel.ERROR);
		}	  
		logger.error("Error al comitear.",t);
		return new UserError("Error interno, tome una captura de pantalla con el error y contáctese con el administrador del sistema.",AbstractErrorMessage.ContentMode.TEXT, ErrorMessage.ErrorLevel.ERROR);
	}

	public static <T extends Throwable> T getCauseOfType(Throwable th, Class<T> type) {
		while (th != null) {
			if (type.isAssignableFrom(th.getClass())) {
				return (T) th;
			} else {
				th = th.getCause();
			}
		}
		return null;
	}
	
	List<MenuItem> menuItems = new ArrayList<MenuItem>(5);

	public void highlightMenuItem(String URL){
		for(MenuItem item : menuItems ){
			if(getUrl(item.getText()).equals(URL))
				item.setStyleName("highlight");
			else
				item.setStyleName(null);
		}
	}
	
	public String getUrl(String menuName){
		if(menuName == null )
			throw new RuntimeException("Nombre de menu sin clase conocida.");

		if(menuName.equals(Constants.MENU_ADMINISTRACION))
			return AdministrationView.NAME;
		else
			throw new RuntimeException("Nombre de menu sin clase conocida.");
	}
}
