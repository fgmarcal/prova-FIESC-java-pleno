package org.fiesc.felipe.api.modules.controller;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.api.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.model.dto.PessoaResponseDto;
import org.fiesc.felipe.api.modules.service.interfaces.PessoaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoa")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<List<PessoaRequestDto>> listar() {
        return ResponseEntity.ok(pessoaService.listarTodos());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaRequestDto> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(pessoaService.consultarPorCpf(cpf));
    }

    @DeleteMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaResponseDto> deletarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(pessoaService.removerPorCpf(cpf));
    }
}
