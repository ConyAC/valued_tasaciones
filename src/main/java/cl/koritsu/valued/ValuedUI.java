package cl.koritsu.valued;

import java.util.Locale;

import org.springframework.context.annotation.Scope;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
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
import cl.koritsu.valued.view.dashboard.DashboardView;

@org.springframework.stereotype.Component
@Scope("prototype")
@Theme("dashboard")
@Widgetset("cl.koritsu.valued.ValuedWidgetSet")
@Title("Valued")
@SuppressWarnings("serial")
public final class ValuedUI extends UI {

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
            // Authenticated user
            setContent(new MainView());
            removeStyleName("loginview");
            String state = getNavigator().getState();
            if(state != null && state.trim().length() == 0 )
            	getNavigator().navigateTo(DashboardView.NAME);
            else
            	getNavigator().navigateTo(getNavigator().getState());
        } else {
            setContent(new LoginView());
            addStyleName("loginview");
        }
    }

    @Subscribe
    public void userLoginRequested(final UserLoginRequestedEvent event) {
        Usuario user = getDataProvider().authenticate(event.getUserName(),
                event.getPassword());
        VaadinSession.getCurrent().setAttribute(Usuario.class.getName(), user);
        updateContent();
    }

    @Subscribe
    public void userLoggedOut(final UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
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
}
