package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import cl.koritsu.valued.domain.Region;

@Repository
public interface RegionRepository extends PagingAndSortingRepository<Region,Long> {

}
