package org.pausanchez.services;

import io.smallrye.mutiny.Uni;
import org.pausanchez.entities.Cliente;
import org.pausanchez.entities.Contacto;

import javax.ws.rs.core.Response;
import java.util.List;

public interface ClientesService {
    Response addCliente(Cliente cliente);
    Response updateCliente(Cliente cliente);
    Response deleteCliente(Long id);
    Response getClientes();
    Response getClienteById(Long id);
    //Uni<Cliente> getCustomerWithProducts(Long id);
    //Uni<List<Product>> getAllProductsGraphQL() throws ExecutionException, InterruptedException;
    //Uni<Product> getProductByIdGraphQL(Long id);
}
