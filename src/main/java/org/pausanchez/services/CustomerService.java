package org.pausanchez.services;

import io.smallrye.mutiny.Uni;
import org.pausanchez.entities.Customer;

import javax.ws.rs.core.Response;
import java.util.List;

public interface CustomerService {
    Uni<Response> addCustomer(Customer customer);
    Uni<Response> updateCustomer(Customer customer);
    Uni<Response> deleteCustomer(Long id);
    Uni<List<Customer>> getCustomers();
    Uni<Customer> getCustomerById(Long id);
    Uni<Customer> getCustomerWithProducts(Long id);
}
