package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.DTO.AvaliacaoRequest;
import com.itb.inf3cn.fitbox.model.entity.Avaliacao;
import com.itb.inf3cn.fitbox.model.services.AvaliacaoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/avaliacoes")
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    // Endpoint legado, mantido para a tela administrativa.
    @GetMapping
    public ResponseEntity<List<Avaliacao>> listar() {
        return ResponseEntity.ok(avaliacaoService.findAll());
    }

    // Avaliações de todos os clientes, com paginação no servidor.
    // Exemplo: /api/v1/avaliacoes/publicas?page=0&size=10
    @GetMapping("/publicas")
    public ResponseEntity<Page<Avaliacao>> listarPublicas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                limitarTamanho(size),
                Sort.by(Sort.Direction.DESC, "dataAvaliacao")
        );
        return ResponseEntity.ok(avaliacaoService.listarPublicas(pageable));
    }

    // Histórico de avaliações do cliente, paginado no banco.
    // Exemplo: /api/v1/avaliacoes/cliente/3?page=0&size=10
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<Avaliacao>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                limitarTamanho(size),
                Sort.by(Sort.Direction.DESC, "dataAvaliacao")
        );
        return ResponseEntity.ok(avaliacaoService.listarPorCliente(clienteId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Avaliacao> salvar(@RequestBody AvaliacaoRequest request) {
        Avaliacao salva = avaliacaoService.save(
                request.getNota(),
                request.getComentario(),
                request.getClienteId(),
                request.getProdutoId(),
                request.getPedidoId()
        );
        return ResponseEntity
                .created(URI.create("/api/v1/avaliacoes/" + salva.getId()))
                .body(salva);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Avaliacao> responder(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String respostaAdmin = body.get("respostaAdmin") != null
                ? body.get("respostaAdmin").toString()
                : null;
        return ResponseEntity.ok(avaliacaoService.responder(id, respostaAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        avaliacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private int limitarTamanho(int size) {
        // Evita que uma chamada peça milhares de registros de uma só vez.
        return Math.min(Math.max(size, 1), 100);
    }
}
