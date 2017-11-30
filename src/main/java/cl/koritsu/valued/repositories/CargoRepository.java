package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cargo;

public interface CargoRepository extends PagingAndSortingRepository<Cargo, Long> {

}
