package dev.camilo.msusuarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-cursos", url = "ms-cursos:8002")
public interface CursoClientRest {

  @DeleteMapping("/desasignar-curso-usuario/{id}")
  void desasignarCursoUsuarioPorId(@PathVariable Long id);
}
