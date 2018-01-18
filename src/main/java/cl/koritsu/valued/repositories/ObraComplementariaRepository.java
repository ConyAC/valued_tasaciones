package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.ObraComplementaria;

public interface ObraComplementariaRepository extends PagingAndSortingRepository<ObraComplementaria, Long> {

	@Query(value="select o from ObraComplementaria where bienId = ?1 ")
	List<ObraComplementaria> findByBien(long bienId);

}
