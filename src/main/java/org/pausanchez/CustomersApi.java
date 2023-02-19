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
    private CustomersRepository customerRepository;

    @Inject
    private Vertx vertx;

    private WebClient webClient;
    @PostConstruct
    void init(){
        webClient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost("localhost")
                        .setDefaultPort(8080).setSsl(false).setTrustAll(true));
    }

    @POST
    @Blocking
    public Response addCustomer(Customer customer) {
        customer.getProducts().forEach(p-> p.setCustomer(customer));
        customerRepository.save(customer);
        return Response.ok().build();
    }

    @PUT
    @Blocking
    public Response updateCustomer(Customer customer){
        customerRepository.save(customer);
        return Response.ok().build();
    }
    @Path("/{id}")
    @DELETE
    @Blocking
    public Response deleteCustomer(@PathParam("id") Long id){
        Customer customer = customerRepository.findById(id).orElseThrow();
        customerRepository.delete(customer);
        return Response.ok().build();
    }

    @GET
    @Blocking
    public List<Customer> getCustomers(){
        return customerRepository.findAll();
    }

    @Path("/{id}")
    @GET
    @Blocking
    public Customer getCustomerById(@PathParam("id") Long id){
        return customerRepository.findById(id).orElseThrow();
    }

    @Path("/{id}/product")
    @GET
    @Blocking
    public Uni<Customer> getProductById(@PathParam("id") Long id){
       return Uni.combine().all().unis(getCustomerReactive(id), getAllProducts())
                .combinedWith((cust,prods)-> {
                    cust.getProducts().forEach(product -> prods.forEach(p-> {
                        if(p.getId().equals(product.getProduct())){
                            product.setName(p.getName());
                            product.setDescription(p.getDescription());
                        }
                    }));
                    return cust;
                });
    }

    private Uni<Customer> getCustomerReactive(Long id){
        Customer customer = customerRepository.findById(id).orElseThrow();

        return Uni.createFrom().item(customer);
    }

    private Uni<List<Product>> getAllProducts(){
        return webClient.get(8080, "localhost", "/product").send()
                .onFailure().invoke(res -> log.error("Error recuperando productos", res))
                .onItem().transform(res -> {
                    log.info("Parseando productos obtenidos");
                    List<Product> productList = new ArrayList<>();
                    JsonArray objects = res.bodyAsJsonArray();

                    objects.forEach(p -> {
                        log.info("Productos {}", p);
                        ObjectMapper objectMapper = new ObjectMapper();
                        Product product;
                        try {
                            product = objectMapper.readValue(p.toString(), Product.class);
                            productList.add(product);
                        } catch (JsonProcessingException e) {
                            log.error("Error parseando product", e);
                        }
                    });
                    return productList;
                });
    }
}