package cl.koritsu.valued.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RutDigitValidator implements ConstraintValidator<RutDigit, String> {

	Logger logger = LoggerFactory.getLogger(RutDigitValidator.class);

	@Override
	public void initialize(RutDigit constraintAnnotation) {

	}

	@Override
	public boolean isValid(String rut, ConstraintValidatorContext context) {
		if (rut == null || rut.trim().length() == 0)
			return true;
		String[] rutTemp = rut.split("-");
		if (rutTemp == null || rutTemp.length != 2)
			return false;
		
		if (rutTemp[1].compareToIgnoreCase(Digito(Integer.valueOf(rutTemp[0]))) == 0 ) {
			return true;
		}
		return false;
	}
	
	public static String Digito(int rut) {
		int suma = 0;
		int multiplicador = 1;
		while (rut != 0) {
			multiplicador++;
			if (multiplicador == 8)
				multiplicador = 2;
			suma += (rut % 10) * multiplicador;
			rut = rut / 10;
		}
		suma = 11 - (suma % 11);
		if (suma == 11) {
			return "0";
		} else if (suma == 10) {
			return "K";
		} else {
			return suma + "";
		}
	}

	/*
	 * Método Estático que valida si un rut es válido Fuente :
	 * http://www.creations.cl/2009/01/generador-de-rut-y-validador/
	 */
	public static boolean ValidarRut(int rut, char dv) {
		int m = 0, s = 1;
		for (; rut != 0; rut /= 10) {
			s = (s + rut % 10 * (9 - m++ % 6)) % 11;
		}
		return dv == (char) (s != 0 ? s + 47 : 75);
	}

	public static boolean validarRut2(String rut) {

		boolean validacion = false;
		try {
			rut = rut.toUpperCase();
			rut = rut.replace(".", "");
			rut = rut.replace("-", "");
			int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

			char dv = rut.charAt(rut.length() - 1);

			int m = 0, s = 1;
			for (; rutAux != 0; rutAux /= 10) {
				s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
			}
			if (dv == (char) (s != 0 ? s + 47 : 75)) {
				validacion = true;
			}

		} catch (java.lang.NumberFormatException e) {
		} catch (Exception e) {
		}
		return validacion;
	}

	public static Boolean validarRut3(final int rutSinVerificador, final int digitoVerificador) {
		int rut = rutSinVerificador; // rut sin dígito verificador.
		int contador = 2;
		int acumulador = 0;
		while (rut != 0) {
			int multiplo = (rut % 10) * contador;
			acumulador = acumulador + multiplo;
			rut = rut / 10;
			contador++;
			if (contador == 8) {
				contador = 2;
			}
		}
		int digitoCorrecto = acumulador % 10;
		// System.out.printf("El dígito verificador es: %d\n", digitoCorrecto);
		return digitoCorrecto == digitoVerificador;
	}

}
