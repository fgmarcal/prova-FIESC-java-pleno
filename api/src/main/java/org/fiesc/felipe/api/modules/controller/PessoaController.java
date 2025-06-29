package org.fiesc.felipe.api.modules.controller;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.api.modules.exceptions.NotFoundException;
import org.fiesc.felipe.api.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.model.dto.PessoaResponseDto;
import org.fiesc.felipe.api.modules.service.interfaces.PessoaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pessoa")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService pessoaService;

    @PostMapping("/")
    public ResponseEntity<PessoaResponseDto> criarPessoa(@RequestBody PessoaRequestDto dto) {
        pessoaService.criarPessoa(dto);
        return ResponseEntity.created(URI.create("/pessoa/cpf/" + dto.cpf())).body(new PessoaResponseDto(null, "Pessoa criada com sucesso"));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PessoaRequestDto>> listar() {
        return ResponseEntity.ok(pessoaService.listarTodos());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaRequestDto> buscarPorCpf(@PathVariable String cpf) throws NotFoundException {
        return ResponseEntity.ok(pessoaService.consultarPorCpf(cpf));
    }

    @DeleteMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaResponseDto> deletarPorCpf(@PathVariable String cpf) throws NotFoundException {
        return ResponseEntity.ok(pessoaService.removerPorCpf(cpf));
    }

    @PutMapping("/cpf/{cpf}")
    public ResponseEntity<Void> atualizarPessoa(@PathVariable String cpf, @RequestBody PessoaRequestDto dto) throws NotFoundException {
        if (!cpf.equals(dto.cpf())) {
            return ResponseEntity.badRequest().build();
        }
        pessoaService.atualizarPessoa(dto);
        return ResponseEntity.ok().build();
    }

}
