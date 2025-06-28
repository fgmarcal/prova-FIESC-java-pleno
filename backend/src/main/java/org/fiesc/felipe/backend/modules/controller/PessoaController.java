package org.fiesc.felipe.backend.modules.controller;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.backend.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.backend.modules.model.dto.PessoaResponseDto;
import org.fiesc.felipe.backend.modules.service.interfaces.PessoaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoa")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService pessoaService;

    @PostMapping
    public ResponseEntity<PessoaResponseDto> salvar(@RequestBody PessoaRequestDto dto) {
        return ResponseEntity.ok(pessoaService.salvar(dto));
    }

    @PutMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaResponseDto> atualizar(@PathVariable String cpf, @RequestBody PessoaRequestDto dto) {
        return ResponseEntity.ok(pessoaService.atualizar(cpf, dto));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaRequestDto> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(pessoaService.consultarPorCpf(cpf));
    }

    @GetMapping
    public ResponseEntity<List<PessoaRequestDto>> listarTodos() {
        return ResponseEntity.ok(pessoaService.listarTodos());
    }

    @DeleteMapping("/cpf/{cpf}")
    public ResponseEntity<Void> deletar(@PathVariable String cpf) {
        pessoaService.remover(cpf);
        return ResponseEntity.noContent().build();
    }
}
