package cl.koritsu.valued.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.ValuedUI;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.EstadoUsuario;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.event.ValuedEvent.UserLoginRequestedEvent;
import cl.koritsu.valued.services.UserService;
import cl.koritsu.valued.view.schedule.AdministrationView;
import cl.koritsu.valued.view.utils.Constants;
import ru.xpoft.vaadin.VaadinView;


@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = LoginView.NAME, cached = true)
public class LoginView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1350686699750559650L;

	public static final String NAME = "";
	
	Button signin;	
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserService userService;
		
	ShortcutListener enter = new ShortcutListener("Entrar",
			KeyCode.ENTER, null) {
		/**
				 * 
				 */
				private static final long serialVersionUID = -2574370594381591577L;

		@Override
		public void handleAction(Object sender, Object target) {
			signin.click();
		}

	};
	
	public LoginView() {
//        setSizeFull();
//        init();
	}
	
	@PostConstruct
    public void init() {
		setSizeFull();
		
        Component loginForm = buildLoginForm();
        addComponent(loginForm);
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

//        Notification notification = new Notification(
//                "Bienvenido a Valued");
//        notification
//                .setDescription("<span>Aplicación en desarrollo para demostrar las funcionalidades a implementar para la empresa Valued.</span> <span>No requiere usuario o contraseña, basta con hacer click en el botón <b>Ingresar</b> para continuar.</span>");
//        notification.setHtmlContentAllowed(true);
//        notification.setStyleName("tray dark small closable login-help");
//        notification.setPosition(Position.BOTTOM_CENTER);
//        notification.setDelayMsec(20000);
//        notification.show(Page.getCurrent());
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

//        signin.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(final ClickEvent event) {
//                ValuedEventBus.post(new UserLoginRequestedEvent(username
//                        .getValue(), password.getValue()));
//            }
//        });
//        return fields;
//    }
        
        signin.addClickListener(new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -6085355474567261970L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				 if (username.isEmpty() || password.isEmpty()) {
					 Notification.show("Deben ser ingresados el usuario y el password ",Type.ERROR_MESSAGE);
		            } 
				 else {
					 try {
						 System.out.println("Hola "+ username +" "+ password );						 
						 Usuario user = userService.findUsuarioByUsername(username.getValue());
						 if(user!= null) {
							 if(user.getEstadoUsuario().equals(EstadoUsuario.DESHABILITADO)) {
								 Notification.show("El usuario "+user.getEmail()+" se encuentra deshabilitado, contacte al Administrador.",Type.WARNING_MESSAGE);
								 return;
							 }
						 }
	                    
					 	 UsernamePasswordAuthenticationToken token = 
	                           new UsernamePasswordAuthenticationToken(String.valueOf(username), String.valueOf(password));						 	

					 	 Authentication authentication = authenticationManager.authenticate(token);
		                   
					 	 // Set the authentication info to context 
					 	 SecurityContext securityContext = SecurityContextHolder.getContext();
					 	 securityContext.setAuthentication(authentication);
					 	 System.out.println("securityContext "+ securityContext.getAuthentication().toString() );	
	                    
					 	 VaadinSession.getCurrent().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

	                  
					 	 VaadinSession.getCurrent().setAttribute(Constants.SESSION_USUARIO, user);
					 	 signin.removeShortcutListener(enter);

//		                    ((ValuedUI)UI.getCurrent()).setVisible(true);
//		                    UI.getCurrent().getNavigator().navigateTo(Administration.NAME);
	                    
					 	 ValuedEventBus.post(new UserLoginRequestedEvent(username.getValue(), password.getValue()));
					}catch (BadCredentialsException e) {
						e.printStackTrace();
						Notification.show("Debe ingresar credenciales validas para el inicio de sesión en el sistema",Type.ERROR_MESSAGE);
					} catch (Exception e) {
						System.out.println("Mal login "+ e.getMessage()+" - "+e.getCause() );
						Notification.show("Se ha producido el siguiente error al intentar ingresar al sistema: "+e.getLocalizedMessage(),Type.ERROR_MESSAGE);
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
		
		((ValuedUI)UI.getCurrent()).setVisible(true);
		//re asigna el enter
		signin.addShortcutListener(enter);
	}
}
