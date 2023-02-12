package dev.camilo.msusuarios.controllers;

import dev.camilo.msusuarios.models.entities.Usuario;
import dev.camilo.msusuarios.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping( "/" )
@RequiredArgsConstructor
public class UsuarioController {

  private final UsuarioService service;

  @GetMapping
  public List <Usuario> listar() {
    return service.listar();
  }

  @GetMapping( "/{id}" )
  public ResponseEntity <?> detalle( @PathVariable Long id ) {

    Optional <Usuario> usuarioOptional = service.porId( id );
    if ( usuarioOptional.isPresent() ) {
      return ResponseEntity.ok( usuarioOptional.get() );
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity <?> crear( @RequestBody Usuario usuario ) {
    return ResponseEntity.status( HttpStatus.CREATED ).body( service.guardar( usuario ) );
  }

  @PutMapping( "/{id}" )
  public ResponseEntity <?> editar( @RequestBody Usuario usuario, @PathVariable Long id ) {
    Optional <Usuario> usuarioOptional = service.porId( id );
    if ( usuarioOptional.isPresent() ) {
      Usuario usuarioDB = usuarioOptional.get();
      usuarioDB.setNombre( usuario.getNombre() );
      usuarioDB.setEmail( usuario.getEmail() );
      usuarioDB.setPassword( usuario.getPassword() );
      return ResponseEntity.status( HttpStatus.CREATED ).body( service.guardar( usuarioDB ) );
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> eliminar(@PathVariable Long id){
    Optional<Usuario> usuarioOptional = service.porId( id );
    if(usuarioOptional.isPresent()){
      service.eliminar( id );
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}
