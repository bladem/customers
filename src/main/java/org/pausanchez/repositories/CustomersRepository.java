package org.pausanchez.repositories;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import org.pausanchez.entities.Customer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomersRepository implements PanacheRepositoryBase<Customer, Long> {
}
