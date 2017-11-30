package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.HonorarioCliente;

public interface HonorarioClienteRepository extends PagingAndSortingRepository<HonorarioCliente, Long> {

}
