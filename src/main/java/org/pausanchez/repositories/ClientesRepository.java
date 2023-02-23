package org.pausanchez.repositories;

import org.pausanchez.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ClientesRepository extends JpaRepository<Cliente, Long> {

}
