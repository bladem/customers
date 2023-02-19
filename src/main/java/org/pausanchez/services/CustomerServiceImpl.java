package org.pausanchez.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.List;


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
    public void addCustomer(Customer customer) {
        customer.getProducts().forEach(p-> p.setCustomer(customer));
        customerRepository.save(customer);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow();
        customerRepository.delete(customer);
    }

    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow();
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
