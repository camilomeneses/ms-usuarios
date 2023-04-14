package dev.camilo.msusuarios.controllers;

import dev.camilo.msusuarios.models.entities.Usuario;
import dev.camilo.msusuarios.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
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

  private final ApplicationContext context;

  private final Environment env;

  @GetMapping("/authorized")
  public Map<String, Object> authorized(@RequestParam String code){
    return Collections.singletonMap("code", code);
  }

  @GetMapping("/crash")
  public void crash(){
    ((ConfigurableApplicationContext)context).close();
  }

  @GetMapping
  public ResponseEntity <?>  listar() {

    Map<String,Object> body = new HashMap<>();
    body.put("users", service.listar());
    // Enviroment desde deployment-usuarios
    body.put("pod_info", env.getProperty("MY_POD_NAME") + ": " + env.getProperty("MY_POD_IP"));

    // Property desde ConfigMap
    body.put("texto", env.getProperty("config.texto"));

    /*return Collections.singletonMap( "users", service.listar());*/
    return ResponseEntity.ok( body );
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

  @GetMapping("/usuarios-por-curso")
  public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids){
    return ResponseEntity.ok(service.listarPorIds( ids ));
  }

  // metodos de la clase
  private static ResponseEntity <Map <String, String>> validar( BindingResult result ) {

    Map <String, String> errores = new HashMap <>();
    result.getFieldErrors().forEach( err -> {
      errores.put( err.getField(), "El campo ".concat( err.getField() ).concat( " " ).concat( err.getDefaultMessage() ) );
    } );
    return ResponseEntity.badRequest().body( errores );
  }
}
