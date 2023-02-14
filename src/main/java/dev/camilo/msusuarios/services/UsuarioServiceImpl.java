package dev.camilo.msusuarios.services;

import dev.camilo.msusuarios.client.CursoClientRest;
import dev.camilo.msusuarios.models.entities.Usuario;
import dev.camilo.msusuarios.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService{

  private final UsuarioRepository repository;

  private final CursoClientRest cursoClient;

  @Override
  @Transactional(readOnly = true)
  public List <Usuario> listar() {
    return ( List <Usuario> ) repository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional <Usuario> porId( Long id ) {
    return repository.findById( id );
  }

  @Override
  @Transactional
  public Usuario guardar( Usuario usuario ) {
    return repository.save( usuario );
  }

  @Override
  @Transactional
  public void eliminar( Long id ) {
    repository.deleteById( id );
    cursoClient.desasignarCursoUsuarioPorId( id );
  }

  @Override
  public Optional <Usuario> porEmail( String email ) {
    return repository.findByEmail( email );
  }

  @Override
  public boolean existePorEmail( String email ) {
    return repository.existsByEmail( email );
  }

  @Override
  public List <Usuario> listarPorIds( Iterable <Long> ids ) {
    return ( List <Usuario> ) repository.findAllById( ids );
  }
}
