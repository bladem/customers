package org.pausanchez.services;

import io.smallrye.mutiny.Uni;
import org.pausanchez.entities.Customer;

import java.util.List;

public interface CustomerService {
    void addCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void deleteCustomer(Long id);
    List<Customer> getCustomers();
    Customer getCustomerById(Long id);
    Uni<Customer> getCustomerWithProducts(Long id);
}
