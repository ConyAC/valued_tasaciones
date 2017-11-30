package cl.koritsu.valued.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Sucursal;

public interface SucursalRepository extends PagingAndSortingRepository<Sucursal, Long> {

	List<Sucursal> findByCliente(Cliente cliente);

}
