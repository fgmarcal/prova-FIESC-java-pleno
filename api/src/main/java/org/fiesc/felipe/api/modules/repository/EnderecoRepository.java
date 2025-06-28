package org.fiesc.felipe.api.modules.repository;

import org.fiesc.felipe.api.modules.model.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
