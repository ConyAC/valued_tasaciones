package cl.koritsu.valued.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cargo;

public interface CargoRepository extends PagingAndSortingRepository<Cargo, Long> {

	@Query("select c from Cargo c where c.llave = 'EJECUTIVO' ")
	Cargo getCargoEjecutivo();

}
