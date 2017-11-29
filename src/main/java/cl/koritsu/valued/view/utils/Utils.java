package cl.koritsu.valued.view.utils;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Field;

public class Utils {

	/**
	 * Permite asegurarse de que el campo no este bindeado antes de bindearlo de nuevo
	 * @param fg
	 * @param field
	 * @param propertyId
	 */
	public static void bind(BeanFieldGroup fg, Field field, String propertyId) {
		if(fg.getField(propertyId) == null)
			fg.bind(field, propertyId);
	}
	
	

}
