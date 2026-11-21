package org.TecNova.repository;

import org.TecNova.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>
{
    List<Categoria> findAllByOrderByNombreAsc( );
}
