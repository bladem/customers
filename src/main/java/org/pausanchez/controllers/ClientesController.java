package org.pausanchez.controllers;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.pausanchez.entities.Cliente;
import org.pausanchez.entities.Contacto;
import org.pausanchez.services.ClientesService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class ClientesController {

    @Inject
    private ClientesService clientesService;

    @POST
    public Response addCustomer(Cliente cliente) {
        return clientesService.addCliente(cliente);
    }

    @PUT
    public Response updateCustomer(Cliente cliente){
        if (cliente == null || cliente.getId() == null) {
            throw new WebApplicationException("Cliente id was not set on request.", HttpResponseStatus.UNPROCESSABLE_ENTITY.code());
        }
        return clientesService.updateCliente(cliente);
    }
    @Path("/{id}")
    @DELETE
    public Response deleteCustomer(@PathParam("id") Long id){
        return clientesService.deleteCliente(id);
    }

    @GET
    public Response getCustomers(){
        return clientesService.getClientes();
    }

    @Path("/{id}")
    @GET
    public Response getCustomerById(@PathParam("id") Long id){
        return clientesService.getClienteById(id);
    }


    @GET
    @Path("/getClientePrueba")
    public Cliente getClientePrueba(){
        Cliente cliente = new Cliente();
        cliente.setNombre("Pau");
        cliente.setApellido1("Sanchez");
        cliente.setContactos(new ArrayList<>());

        Contacto contacto = new Contacto();
        contacto.setMail("bladem@gmail.com");
        contacto.setTelefono1("663904289");
        cliente.getContactos().add(contacto);

        return cliente;
    }

    /*
    @Path("/{id}/product")
    @GET
    public Uni<Cliente> getProductById(@PathParam("id") Long id){
       return clientesService.getCustomerWithProducts(id);
    }

    @Path("/getAllProductsGraphQL")
    @GET
    public Uni<List<Product>> getAllProductsGraphQL() throws ExecutionException, InterruptedException {
        return clientesService.getAllProductsGraphQL();
    }

    @Path("/{id}/productQL")
    @GET
    public Uni<Product> getProductByIdGraphQL(@PathParam("id") Long id){
        return clientesService.getProductByIdGraphQL(id);
    }
    */
}