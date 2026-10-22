package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.model.entity.Pedido;
import com.itb.inf3cn.fitbox.model.services.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Pedido> salvar(@RequestBody Pedido pedido) {
        return ResponseEntity.ok(pedidoService.save(pedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        return ResponseEntity.ok(pedidoService.update(id, pedido));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pedido> atualizarStatusAntigo(@PathVariable Long id, @RequestBody Pedido pedido) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, pedido.getStatus()));
    }

    @PatchMapping("/{id}/acompanhamento")
    public ResponseEntity<Pedido> atualizarAcompanhamento(
            @PathVariable Long id,
            @RequestBody AtualizarAcompanhamentoRequest request) {
        return ResponseEntity.ok(
                pedidoService.atualizarAcompanhamento(id, request.getStatus(), request.getMinutosEstimados())
        );
    }

    /*
     * Cancelamento lógico: mantém o pedido no banco para que o cliente
     * veja o aviso e o administrador preserve o histórico.
     * O serviço valida método de pagamento e prazo de 15 minutos.
     */
    @PatchMapping("/{id}/cancelar-pix")
    public ResponseEntity<Pedido> cancelarPixPorPrazo(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPixPorPrazo(id));
    }

    /*
     * Mantido por compatibilidade com o projeto existente.
     * Não usar este endpoint para o cancelamento por prazo de PIX.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
