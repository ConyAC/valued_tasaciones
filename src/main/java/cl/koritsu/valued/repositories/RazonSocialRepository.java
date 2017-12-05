package cl.koritsu.valued.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.RazonSocial;

public interface RazonSocialRepository extends PagingAndSortingRepository<RazonSocial, Long> {

	@Modifying
	@Query("delete from RazonSocial where cliente = ?1 ")
	void deleteByCliente(Cliente cliente);

}
