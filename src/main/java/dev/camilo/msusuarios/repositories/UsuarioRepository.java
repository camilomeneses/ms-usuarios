package dev.camilo.msusuarios.repositories;

import dev.camilo.msusuarios.models.entities.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

  Optional<Usuario> findByEmail(String email);

  /* Alternativa usando @Query */
  @Query("select u from Usuario u where u.email=?1")
  Optional<Usuario> porEmail(String email);

  /* Alternativa usando exists */
  boolean existsByEmail(String email);
}
