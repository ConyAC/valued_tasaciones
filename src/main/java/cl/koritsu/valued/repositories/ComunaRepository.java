package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.Region;

@Repository
public interface ComunaRepository extends PagingAndSortingRepository<Comuna, Long> {

	List<Comuna> findByRegion(Region region);

}
