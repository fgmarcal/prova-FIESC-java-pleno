package org.fiesc.felipe.backend.modules.infrastructure.repository;

import org.fiesc.felipe.backend.modules.domain.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
