package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.TipoOperacion;

public interface TipoOperacionRepository extends PagingAndSortingRepository<TipoOperacion, Long> {

}
