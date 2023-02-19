package org.pausanchez;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.pausanchez.entities.Customer;
import org.pausanchez.entities.Product;
import org.pausanchez.repositories.CustomersRepository;
import org.pausanchez.services.CustomerService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class CustomersApi {

    @Inject
    private CustomerService customerService;

    @POST
    @Blocking
    public Response addCustomer(Customer customer) {
        customerService.addCustomer(customer);
        return Response.ok().build();
    }

    @PUT
    @Blocking
    public Response updateCustomer(Customer customer){
        customerService.updateCustomer(customer);
        return Response.ok().build();
    }
    @Path("/{id}")
    @DELETE
    @Blocking
    public Response deleteCustomer(@PathParam("id") Long id){
        customerService.deleteCustomer(id);
        return Response.ok().build();
    }

    @GET
    @Blocking
    public List<Customer> getCustomers(){
        return customerService.getCustomers();
    }

    @Path("/{id}")
    @GET
    @Blocking
    public Customer getCustomerById(@PathParam("id") Long id){
        return customerService.getCustomerById(id);
    }

    @Path("/{id}/product")
    @GET
    @Blocking
    public Uni<Customer> getProductById(@PathParam("id") Long id){
       return customerService.getCustomerWithProducts(id);
    }
}