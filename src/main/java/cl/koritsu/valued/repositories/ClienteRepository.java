package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Cliente;

public interface ClienteRepository extends PagingAndSortingRepository<Cliente, Long> {

}
