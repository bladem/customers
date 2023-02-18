package org.pausanchez;

import org.pausanchez.entities.Customer;
import org.pausanchez.repositories.CustomerRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/customers")
public class CustomersApi {

    @Inject
    private CustomerRepository customerRepository;

    @POST
    public Response addCustomer(Customer customer) {
        customerRepository.addCustomer(customer);
        return Response.ok().build();
    }

    @PUT
    public Response updateCustomer(Customer customer){
        customerRepository.updateCustomer(customer);
        return Response.ok().build();
    }

    @GET
    public List<Customer> getCustomers(){
        return customerRepository.getCustomers();
    }
}