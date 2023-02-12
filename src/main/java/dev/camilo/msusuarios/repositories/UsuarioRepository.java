package dev.camilo.msusuarios.repositories;

import dev.camilo.msusuarios.models.entities.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
}
