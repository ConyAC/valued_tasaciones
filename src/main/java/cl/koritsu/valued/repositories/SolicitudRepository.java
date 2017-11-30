package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.SolicitudTasacion;

public interface SolicitudRepository extends PagingAndSortingRepository<SolicitudTasacion, Long> {

}
