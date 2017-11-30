package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Bien;

public interface BienRepository extends PagingAndSortingRepository<Bien, Long> {

}
