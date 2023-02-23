package org.pausanchez.services;


import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.pausanchez.entities.Cliente;
import org.pausanchez.entities.Contacto;
import org.pausanchez.repositories.ClientesRepository;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;


@ApplicationScoped
@Slf4j
public class ClientesServiceImpl implements ClientesService {

    @Inject
    private ClientesRepository clientesRepository;

    @Inject
    private Vertx vertx;

    private WebClient webClient;

    @Inject
    @GraphQLClient("product-dynamic-client")
    private DynamicGraphQLClient graphQLClient;

    @PostConstruct
    void init(){
        webClient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost("localhost")
                        .setDefaultPort(8080).setSsl(false).setTrustAll(true));
    }

    @Override
    public Response addCliente(Cliente cliente) {
        if(cliente.getContactos()!=null) {
            cliente.getContactos().forEach(contacto -> contacto.setCliente(cliente));
        }

        Cliente clienteSaved = clientesRepository.save(cliente);

        return clienteSaved!=null?Response.ok(clienteSaved).status(CREATED).build():Response.noContent().build();
    }

    @Override
    public Response updateCliente(Cliente cliente) {
        Cliente clientBD = clientesRepository.findById(cliente.getId()).orElse(null);

        if(clientBD!=null){
            clientBD.setNombre(cliente.getNombre());
            clientBD.setApellido1(cliente.getApellido1());
            clientBD.setApellido2(cliente.getApellido2());
            clientBD.setNumeroIdentificacion(cliente.getNumeroIdentificacion());
            List<Contacto> contactosBD = clientBD.getContactos();

            contactosBD.stream().forEach(cBD-> {
                cliente.getContactos().stream().filter(c-> c.getId()!=null).forEach(cN->{
                    if(cN.getId().compareTo(cBD.getId())==0){
                        cBD.setMail(cN.getMail());
                        cBD.setDireccion(cN.getDireccion());
                        cBD.setLocalidad(cN.getLocalidad());
                        cBD.setProvincia(cN.getProvincia());
                        cBD.setTelefono1(cN.getTelefono1());
                        cBD.setCodigoPostal(cN.getCodigoPostal());
                        cBD.setTelefono2(cN.getTelefono2());
                    }
                });
            });
            cliente.getContactos().stream()
                    .filter(c-> c.getId()==null||c.getId().compareTo(0L)==0)
                    .forEach(contactosBD::add);

            return createResponseOk(clientesRepository.save(clientBD));
        }else{
            return createResponseNotFound();
        }
    }

    @Override
    public Response deleteCliente(Long id) {
        Cliente cliente = clientesRepository.findById(id).orElse(null);
        if(cliente != null) {
            clientesRepository.deleteById(id);
            return createResponseOk(id);
        }else{
            return createResponseNotFound();
        }
    }

    @Override
    public Response getClientes() {
        List<Cliente> list = clientesRepository.findAll();
        return list!=null&&!list.isEmpty()?createResponseOk(list):createResponseNotFound();
    }

    @Override
    public Response getClienteById(Long id) {
        return clientesRepository.findById(id).map(this::createResponseOk).orElse(createResponseNotFound());
    }

    private Response createResponseOk(Object entity) {
        return Response.status(Response.Status.OK)
                .entity(entity)
                .build();
    }

    private Response createResponseNotFound() {
        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }


    /*
    @Override
    public Uni<Cliente> getCustomerWithProducts(Long id) {
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

    @Override
    public Uni<List<Product>> getAllProductsGraphQL() throws ExecutionException, InterruptedException {
        log.info("Recuperando todos los productos con graphQL");
        Document query = document(
                operation(
                        field("allProducts",
                                field("id"),
                                field("code"),
                                field("name"),
                                field("description"))
                )
        );
        Uni<io.smallrye.graphql.client.Response> responseUni = graphQLClient.executeAsync(query);

        return responseUni.onItem().transform(response -> response.getList(Product.class, "allProducts"));
    }

    @Override
    public Uni<Product> getProductByIdGraphQL(Long id) {
        Document query = document(
                operation(
                        field("productById(productId:"+id+")",
                                field("id"),
                                field("code"),
                                field("name"),
                                field("description"))
                )
        );
        Uni<io.smallrye.graphql.client.Response> responseUni = graphQLClient.executeAsync(query);

        return responseUni.onItem().transform(response -> response.getObject(Product.class, "productById"));
    }

    private Uni<Cliente> getCustomerReactive(Long id){
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
    }*/
}
