package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.model.entity.ItemPedido;
import com.itb.inf3cn.fitbox.model.services.ItemPedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/itens-pedido")
@CrossOrigin(origins = "*")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @GetMapping
    public ResponseEntity<List<ItemPedido>> listar() {
        return ResponseEntity.ok(itemPedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(itemPedidoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ItemPedido> salvar(@RequestBody ItemPedido itemPedido) {
        return ResponseEntity.ok(itemPedidoService.save(itemPedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPedido> atualizar(
            @PathVariable Long id,
            @RequestBody ItemPedido itemPedido) {

        return ResponseEntity.ok(itemPedidoService.update(id, itemPedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        itemPedidoService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
