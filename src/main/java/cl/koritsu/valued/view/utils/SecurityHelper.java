package cl.koritsu.valued.view.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;

import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.Permiso;

public class SecurityHelper {
	
	private transient static Logger logger = LoggerFactory.getLogger(SecurityHelper.class);
	
	static Usuario testUser;
	
	private static Usuario getUser(){
		//solo para termino de testing
		if(VaadinSession.getCurrent() != null )
			return (Usuario) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO);
		else
			return testUser;
	}
	
	public static void setUser(Usuario user){
		testUser = user;
	}
	
	public static boolean isLogged(){
		Usuario user = getUser();
		logger.debug("user {}",user);
		return  user != null;
	}

	public static boolean hasPermission(Permiso... permissions) {
		if(permissions == null)
			return true;

		Usuario usuario = getUser();
		//si el usuario el nulo, lo rederidige al login
//		if(usuario == null ) {
//			UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
//			return false;
//		}
		
//		if(usuario.getRol() == null || usuario.getRol().getPermission() == null ){
//			return false;
//		}
		
//		for(Permission p : permissions){
//			if(!usuario.getRol().getPermission().contains(p))
//				return false;
//		}

		return true;
	}

	public static boolean hasMenu(String text) {
		Usuario usuario = getUser();
		//si el usuario el nulo, lo rederidige al login
		if(usuario == null ) 
			return true;

		return true;
	}

	public static Usuario getCredentials() {
		Usuario user = getUser();
//		if(user == null ) {
//			UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
//			return null;
//		}
		return user;
	}
}
