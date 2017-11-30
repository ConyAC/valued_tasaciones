package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Contacto;

public interface ContactoRepository extends PagingAndSortingRepository<Contacto, Long> {

	List<Contacto> findByCliente(Cliente cliente);

}
