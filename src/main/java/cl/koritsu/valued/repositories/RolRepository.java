package cl.koritsu.valued.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.valued.domain.Rol;

public interface RolRepository extends PagingAndSortingRepository<Rol, Long> {

}
