package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Contacto;

public interface ContactoRepository extends PagingAndSortingRepository<Contacto, Long> {

	@Query("select c from Contacto c join c.cargo ca where c.cliente = ?1 and ca.llave = 'EJECUTIVO' ")
	List<Contacto> findEjecutivosByCliente(Cliente cliente);

}
