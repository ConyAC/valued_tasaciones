package cl.koritsu.valued.view.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class Utils {

	static DecimalFormatSymbols decimalFormatSymbols;
	static DecimalFormat decimalFormatSinDecimal;
	
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
	
	
	public static void validateEditor(String msj,CommitException e) {
		e.printStackTrace();
		Map<Field<?>, Validator.InvalidValueException> invalidFields = e.getInvalidFields();
		if(!invalidFields.isEmpty()) {
			for(Entry<Field<?>, InvalidValueException> entry : invalidFields.entrySet() ) {
				
				String msj2 = entry.getValue().getMessage();
				if( msj2 == null && entry.getValue().getCauses() != null && entry.getValue().getCauses().length > 0 )
					msj2 = entry.getValue().getCauses()[0].getMessage();
				if(msj2 == null )
					msj2 = "";
				
				Notification.show("Error al guardar "+msj+" debido a : \""+msj2+"\"",Type.ERROR_MESSAGE);
				entry.getKey().focus();
				return;
			}
		}else {
			Notification.show("Error al guardar "+msj+" debido a "+e.getMessage()+"(2)",Type.ERROR_MESSAGE);
		}
	}
	
	public static String formatoFecha(Date date) {
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		
		return format.format(date);
	}

	public static DecimalFormatSymbols getDecimalFormatSymbols(){
		if(decimalFormatSymbols == null){
			decimalFormatSymbols = new DecimalFormatSymbols();
			decimalFormatSymbols.setDecimalSeparator(',');
			decimalFormatSymbols.setGroupingSeparator('.');
		}
		return decimalFormatSymbols;
	}
	
	public static DecimalFormat getDecimalFormatSinDecimal(){
		if(decimalFormatSinDecimal == null){
			decimalFormatSinDecimal = new DecimalFormat("#,###", getDecimalFormatSymbols());
		}
		return decimalFormatSinDecimal;
	}
	
}
