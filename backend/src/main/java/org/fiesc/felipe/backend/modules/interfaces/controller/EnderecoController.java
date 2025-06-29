package org.fiesc.felipe.backend.modules.interfaces.controller;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.backend.modules.shared.dto.EnderecoDto;
import org.fiesc.felipe.backend.modules.infrastructure.external.CorreiosIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/endereco")
@RequiredArgsConstructor
public class EnderecoController {

    private final CorreiosIntegrationService correiosService;

    @GetMapping("/cep/{cep}")
    public ResponseEntity<EnderecoDto> buscarEnderecoPorCep(@PathVariable String cep) {
        EnderecoDto dto = correiosService.buscarEnderecoPorCep(cep);
        return ResponseEntity.ok(dto);
    }
}
