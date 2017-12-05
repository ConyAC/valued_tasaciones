package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cargo;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Contacto;

public interface ContactoRepository extends PagingAndSortingRepository<Contacto, Long> {

	@Query("select c from Contacto c join c.cargo ca where c.cliente = ?1 and ca.llave = 'EJECUTIVO' ")
	List<Contacto> findEjecutivosByCliente(Cliente cliente);

	@Modifying
	@Query("delete from Contacto where cliente = ?1 and cargo != ?2")
	void deleteByClienteNotEjecutivo(Cliente cliente,Cargo cargo);

}
