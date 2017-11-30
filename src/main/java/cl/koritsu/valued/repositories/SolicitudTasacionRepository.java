package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.SolicitudTasacion;

public interface SolicitudTasacionRepository extends PagingAndSortingRepository<SolicitudTasacion, Long> {

}
