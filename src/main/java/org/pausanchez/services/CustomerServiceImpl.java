package org.pausanchez.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.Panache;
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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;


@ApplicationScoped
@Slf4j
public class CustomerServiceImpl implements CustomerService{

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

    @Override
    public Uni<Response> addCustomer(Customer customer) {
        customer.getProducts().forEach(p-> p.setCustomer(customer));

        return Panache.withTransaction(()-> customerRepository.persist(customer))
                .replaceWith(Response.ok(customer).status(CREATED)::build);
    }

    @Override
    public Uni<Response> updateCustomer(Customer customer) {
        return Panache
                .withTransaction(() -> customerRepository.findById(customer.getId())
                        .onItem().ifNotNull().invoke(entity -> {
                            entity.setNames(customer.getNames());
                            entity.setAccountNumber(customer.getAccountNumber());
                            entity.setCode(customer.getCode());
                        })
                )
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @Override
    public Uni<Response> deleteCustomer(Long id) {
        return Panache.withTransaction(() -> customerRepository.deleteById(id))
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

    @Override
    public Uni<List<Customer>> getCustomers() {
        return customerRepository.findAll().list();
    }

    @Override
    public Uni<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Uni<Customer> getCustomerWithProducts(Long id) {
        return Uni.combine().all().unis(getCustomerReactive(id), getAllProducts())
                .combinedWith((cust,prods)-> {
                    cust.getProducts().forEach(product -> prods.forEach(p-> {
                        if(p.getId().equals(product.getProduct())){
                            product.setName(p.getName());
                            product.setDescription(p.getDescription());
                            product.setCode(p.getCode());
                        }
                    }));
                    return cust;
                });
    }

    private Uni<Customer> getCustomerReactive(Long id){
        log.info("Obteniendo customers");
        return customerRepository.findById(id);
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
