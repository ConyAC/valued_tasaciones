package cl.koritsu.valued.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import cl.koritsu.valued.domain.ValorUF;

@Repository
public interface ValorUFRepository extends PagingAndSortingRepository<ValorUF, Long> {

	@Query(value="SELECT v.valor FROM ValorUF v WHERE v.fecha = ?1 ")
	Double getValorUF(Date fecha);


}
