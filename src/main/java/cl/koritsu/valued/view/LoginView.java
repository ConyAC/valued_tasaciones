package cl.koritsu.valued.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.ValuedUI;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.event.ValuedEvent.UserLoginRequestedEvent;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.dashboard.DashboardView;
import cl.koritsu.valued.view.utils.Constants;
import cl.koritsu.valued.event.ValuedEventBus;
import ru.xpoft.vaadin.VaadinView;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = LoginView.NAME, cached = true)
public class LoginView extends VerticalLayout {

	public static final String NAME = "";
	
	Button signin;	
	@Autowired
	private transient AuthenticationManager authenticationManager;
	@Autowired
	private transient ValuedService service;
	
	ShortcutListener enter = new ShortcutListener("Entrar",
			KeyCode.ENTER, null) {
		@Override
		public void handleAction(Object sender, Object target) {
			signin.click();
		}

	};
	
    public LoginView() {
        setSizeFull();

        Component loginForm = buildLoginForm();
        addComponent(loginForm);
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

        Notification notification = new Notification(
                "Bienvenido a Valued");
        notification
                .setDescription("<span>Aplicación en desarrollo para demostrar las funcionalidades a implementar para la empresa Valued.</span> <span>No requiere usuario o contraseña, basta con hacer click en el botón <b>Ingresar</b> para continuar.</span>");
        notification.setHtmlContentAllowed(true);
        notification.setStyleName("tray dark small closable login-help");
        notification.setPosition(Position.BOTTOM_CENTER);
        notification.setDelayMsec(20000);
        notification.show(Page.getCurrent());
    }

    private Component buildLoginForm() {
        final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setSpacing(true);
        Responsive.makeResponsive(loginPanel);
        loginPanel.addStyleName("login-panel");
        loginPanel.addComponent(buildLabels());
        loginPanel.addComponent(buildFields());
        //loginPanel.addComponent(new CheckBox("Remember me", true));
        return loginPanel;
    }

    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout();
        fields.setSpacing(true);
        fields.addStyleName("fields");

        final TextField username = new TextField("Usuario");
        username.setIcon(FontAwesome.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final PasswordField password = new PasswordField("Contraseña");
        password.setIcon(FontAwesome.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        signin = new Button("Ingresar");
        signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(KeyCode.ENTER);
        signin.focus();

        fields.addComponents(username, password, signin);
        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

//        signin.addClickListener(new ClickListener() {
//            @Override
//            public void buttonClick(final ClickEvent event) {
//                ValuedEventBus.post(new UserLoginRequestedEvent(username
//                        .getValue(), password.getValue()));
//            }
//        });
        
        signin.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				String u = username.getValue();
				String p = password.getValue();
				
				 if (u.isEmpty() || p.isEmpty()) {
					 Notification.show("Deben ser ingresados el usuario y el password ",Type.ERROR_MESSAGE);
		            } 
				 else {
					 try {
						 System.out.println("Holi "+ username +" "+ password );
						 	UsernamePasswordAuthenticationToken token = 
		                            new UsernamePasswordAuthenticationToken(u, p);
						 	System.out.println("token "+ token );
		                    Authentication authentication = authenticationManager.authenticate(token);
		                    System.out.println("authentication "+ authentication );
		                    // Set the authentication info to context 
		                    SecurityContext securityContext = SecurityContextHolder.getContext();
		                    securityContext.setAuthentication(authentication);
		                    VaadinSession.getCurrent().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
		                    //busca el usuario en base de datos para guardarlo en la session
		                    Usuario user = service.findUsuarioByUsername(u);
		                    System.out.println("user "+ user );
		                    VaadinSession.getCurrent().setAttribute(Constants.SESSION_USUARIO, user);
		                    System.out.println("Login authentication "+authentication);
		                    signin.removeShortcutListener(enter);

		                    ((ValuedUI)UI.getCurrent()).setVisible(true);
		                    UI.getCurrent().getNavigator().navigateTo(DashboardView.NAME);
		                    
					} catch (Exception e) {
						System.out.println("Mal login "+ e.toString() );
	                    // Add new error message
	                    Label error = new Label(
	                            e.getMessage(),
	                            ContentMode.HTML);
	                    error.addStyleName("error");
	                    error.setSizeUndefined();
	                    error.addStyleName("light");
	                    // Add animation
	                    error.addStyleName("v-animate-reveal");
	                    //loginPanel.addComponent(error);
	                    username.focus();

					}
				 }
			}
		});
        
        return fields;
    }

    private Component buildLabels() {
        CssLayout labels = new CssLayout();
        labels.addStyleName("labels");

        Label welcome = new Label("Bienvenido");
        welcome.setSizeUndefined();
        welcome.addStyleName(ValoTheme.LABEL_H4);
        welcome.addStyleName(ValoTheme.LABEL_COLORED);
        labels.addComponent(welcome);

        Label title = new Label("Valued");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        labels.addComponent(title);
        return labels;
    }
    
	public void enter(ViewChangeEvent event) {
		
		((ValuedUI)UI.getCurrent()).setVisible(false);
		//re asigna el enter
		signin.addShortcutListener(enter);
		
	}

}
