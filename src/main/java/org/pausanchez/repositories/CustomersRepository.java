package org.pausanchez.repositories;

import org.pausanchez.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface CustomersRepository extends JpaRepository<Customer, Long> {
}
