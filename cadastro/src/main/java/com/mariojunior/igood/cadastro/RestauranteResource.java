package com.mariojunior.igood.cadastro;

import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Restaurante")
public class RestauranteResource {
    @GET
    public List<Restaurante> listarRestaurantes() {
        return Restaurante.listAll();
    }
    
    @POST
    @Transactional
    public Response adicionarRestaurante(Restaurante dto) {
    	dto.persist();
    	return Response.status(Status.CREATED).build();
    }
    
    @PUT
    @Path("{id}")
    @Transactional
    public void atualizarRestaurante(@PathParam("id") Long id, Restaurante dto) {
    	Optional<PanacheEntityBase> restauranteOp = Restaurante.findByIdOptional(id);
    	if(restauranteOp.isEmpty()) {
    		throw new NotFoundException();
    	}
    	
    	Restaurante restaurante = (Restaurante) restauranteOp.get();
    	
    	restaurante.nome = dto.nome;
    	restaurante.persist();
    }
    
    @DELETE
    @Path("{id}")
    @Transactional
    public void excluirRestaurante(@PathParam("id") Long id) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(id);
    	restauranteOp.ifPresentOrElse(Restaurante::delete, () -> {
    		throw new NotFoundException();
		});
    }
    
    @GET
    @Path("{idRestaurante}/pratos")
    @Tag(name="Prato")
    public List<Prato> listarPratos(@PathParam("idRestaurante") Long idRestaurante) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
        if(restauranteOp.isEmpty()) {
        	throw new NotFoundException("O restaurante não existe.");
        }
        return Prato.list("restaurante", restauranteOp.get());
    }
    
    @POST
    @Path("{idRestaurante}/pratos")
    @Tag(name="Prato")
    @Transactional
    public Response adicionarPrato(@PathParam("idRestaurante") Long idRestaurante, Prato dto) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
    	if(restauranteOp.isEmpty()) {
    		throw new NotFoundException("Restaurante não existe.");
    	}
    	
    	Prato prato = new Prato();
    	prato.nome = dto.nome;
    	prato.descricao = dto.descricao;
    	prato.preco = dto.preco;
    	prato.restaurante = restauranteOp.get();
    	prato.persist();
    	return Response.status(Status.CREATED).build();
    }
    
    @PUT
    @Path("{idRestaurante}/pratos/{id}")
    @Tag(name="Prato")
    @Transactional
    public void atualizarPrato(@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long id, Prato dto) {
    	Optional<PanacheEntityBase> restauranteOp = Prato.findByIdOptional(idRestaurante);
    	if(restauranteOp.isEmpty()) {
    		throw new NotFoundException("Restaurante não existe.");
    	}
    	
    	Optional<Prato> pratoOp = Prato.findByIdOptional(id);
    
    	if(pratoOp.isEmpty()) {
    		throw new NotFoundException("Prato não existe.");
    	}
    	
    	Prato prato = pratoOp.get();
    	prato.preco = dto.preco;
    	prato.persist();
    }
    
    @DELETE
    @Path("{idRestaurante}/pratos/{id}")
    @Tag(name="Prato")
    @Transactional
    public void excluirPrato(@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long id) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
    	if(restauranteOp.isEmpty()) {
    		throw new NotFoundException("Restaurante não existe.");
    	}
    	Optional<Prato> pratoOp = Prato.findByIdOptional(id);
    	pratoOp.ifPresentOrElse(Prato::delete, () -> {
    		throw new NotFoundException();
		});
    }
}