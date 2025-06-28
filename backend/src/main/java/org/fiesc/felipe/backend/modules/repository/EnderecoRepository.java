package org.fiesc.felipe.backend.modules.repository;

import org.fiesc.felipe.backend.modules.model.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
