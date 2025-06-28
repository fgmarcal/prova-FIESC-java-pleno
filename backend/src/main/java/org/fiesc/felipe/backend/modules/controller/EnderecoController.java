package org.fiesc.felipe.backend.modules.controller;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.backend.modules.model.dto.EnderecoDto;
import org.fiesc.felipe.backend.modules.service.external.CorreiosIntegrationService;
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
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}
