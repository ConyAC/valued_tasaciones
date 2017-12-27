package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Solicitante;

public interface SolicitanteRepository extends PagingAndSortingRepository<Solicitante, Long> {

	List<Solicitante> findByCliente(Cliente cliente);

	Solicitante findByRut(String rut);

}
