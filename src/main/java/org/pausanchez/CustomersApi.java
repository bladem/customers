package org.pausanchez;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.pausanchez.entities.Customer;
import org.pausanchez.services.CustomerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class CustomersApi {

    @Inject
    private CustomerService customerService;

    @POST
    public Response addCustomer(Customer customer) {
        customerService.addCustomer(customer);
        return Response.ok().build();
    }

    @PUT
    public Response updateCustomer(Customer customer){
        if (customer == null || customer.getCode() == null) {
            throw new WebApplicationException("Product code was not set on request.", HttpResponseStatus.UNPROCESSABLE_ENTITY.code());
        }
        customerService.updateCustomer(customer);
        return Response.ok().build();
    }
    @Path("/{id}")
    @DELETE
    public Response deleteCustomer(@PathParam("id") Long id){
        customerService.deleteCustomer(id);
        return Response.ok().build();
    }

    @GET
    public Uni<List<Customer>> getCustomers(){
        return customerService.getCustomers();
    }

    @Path("/{id}")
    @GET
    public Uni<Customer> getCustomerById(@PathParam("id") Long id){
        return customerService.getCustomerById(id);
    }

    @Path("/{id}/product")
    @GET
    public Uni<Customer> getProductById(@PathParam("id") Long id){
       return customerService.getCustomerWithProducts(id);
    }
}