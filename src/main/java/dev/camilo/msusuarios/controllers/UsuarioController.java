package dev.camilo.msusuarios.controllers;

import dev.camilo.msusuarios.models.entities.Usuario;
import dev.camilo.msusuarios.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
  public ResponseEntity <?> crear( @Valid @RequestBody Usuario usuario, BindingResult result ) {

    if ( result.hasErrors() ) {
      return validar( result );
    }
    /* Usando Altenativa de validacion existencia por Email */
    if(!usuario.getEmail().isEmpty() && service.existePorEmail( usuario.getEmail() )){
      return ResponseEntity.badRequest()
          .body( Collections.singletonMap("email","Ya existe un usuario registrado con ese Email"  ) );
    }
    return ResponseEntity.status( HttpStatus.CREATED ).body( service.guardar( usuario ) );
  }

  @PutMapping( "/{id}" )
  public ResponseEntity <?> editar( @Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id ) {

    if ( result.hasErrors() ) {
      return validar( result );
    }
    Optional <Usuario> usuarioOptional = service.porId( id );
    if ( usuarioOptional.isPresent() ) {
      Usuario usuarioDB = usuarioOptional.get();

      if(!usuario.getEmail().isEmpty() && !usuario.getEmail().equalsIgnoreCase( usuarioDB.getEmail() ) && service.porEmail( usuario.getEmail() ).isPresent()){
        return ResponseEntity.badRequest()
            .body( Collections.singletonMap("email","Ya existe un usuario registrado con ese Email"  ) );
      }
      usuarioDB.setNombre( usuario.getNombre() );
      usuarioDB.setEmail( usuario.getEmail() );
      usuarioDB.setPassword( usuario.getPassword() );
      return ResponseEntity.status( HttpStatus.CREATED ).body( service.guardar( usuarioDB ) );
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping( "/{id}" )
  public ResponseEntity <?> eliminar( @PathVariable Long id ) {

    Optional <Usuario> usuarioOptional = service.porId( id );
    if ( usuarioOptional.isPresent() ) {
      service.eliminar( id );
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  private static ResponseEntity <Map <String, String>> validar( BindingResult result ) {

    Map <String, String> errores = new HashMap <>();
    result.getFieldErrors().forEach( err -> {
      errores.put( err.getField(), "El campo ".concat( err.getField() ).concat( " " ).concat( err.getDefaultMessage() ) );
    } );
    return ResponseEntity.badRequest().body( errores );
  }
}
