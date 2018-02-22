package cl.koritsu.valued.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.koritsu.valued.domain.enums.EstadoUsuario;

@Converter(autoApply=true)
public class EstadoUsuarioConverter implements AttributeConverter<EstadoUsuario, Integer> {

	@Override
	public Integer convertToDatabaseColumn(EstadoUsuario arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public EstadoUsuario convertToEntityAttribute(Integer arg0) {
		return EstadoUsuario.getUserStatus(arg0);
	}
}
