package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.DTO.AvaliacaoRequest;
import com.itb.inf3cn.fitbox.model.entity.Avaliacao;
import com.itb.inf3cn.fitbox.model.services.AvaliacaoService;
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

    // LISTAR TODAS (usado pelo admin, e o cliente filtra a própria no front)
    @GetMapping
    public ResponseEntity<List<Avaliacao>> listar() {

        return ResponseEntity.ok(
                avaliacaoService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                avaliacaoService.findById(id)
        );
    }

    // CLIENTE ENVIA UMA AVALIAÇÃO
    @PostMapping
    public ResponseEntity<Avaliacao> salvar(
            @RequestBody AvaliacaoRequest request) {

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

    // ADMIN RESPONDE
    @PatchMapping("/{id}")
    public ResponseEntity<Avaliacao> responder(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        String respostaAdmin =
                body.get("respostaAdmin") != null
                        ? body.get("respostaAdmin").toString()
                        : null;

        return ResponseEntity.ok(
                avaliacaoService.responder(id, respostaAdmin)
        );
    }

    // EXCLUIR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        avaliacaoService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
