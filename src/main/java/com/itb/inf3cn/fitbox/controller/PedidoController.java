package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.exceptions.BadRequest;
import com.itb.inf3cn.fitbox.model.entity.Cliente;
import com.itb.inf3cn.fitbox.model.entity.Pedido;
import com.itb.inf3cn.fitbox.model.services.ClienteService;
import com.itb.inf3cn.fitbox.model.services.PedidoService;
import com.itb.inf3cn.fitbox.util.DistanciaUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    public PedidoController(
            PedidoService pedidoService,
            ClienteService clienteService) {

        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
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

        // ==========================================
        // VALIDAR RAIO DE ENTREGA ANTES DE SALVAR
        // ==========================================

        if (pedido.getCliente() != null
                && pedido.getCliente().getId() != null) {

            Cliente cliente =
                    clienteService.findById(
                            pedido.getCliente().getId()
                    );

            if (cliente.getLatitude() == null
                    || cliente.getLongitude() == null) {

                throw new BadRequest(
                        "Não foi possível confirmar sua localização. "
                                + "Verifique seu endereço cadastrado em 'Meu Endereço'."
                );
            }

            boolean dentroDoRaio =
                    DistanciaUtils.dentroDoRaioDeEntrega(
                            cliente.getLatitude(),
                            cliente.getLongitude()
                    );

            if (!dentroDoRaio) {

                double distancia =
                        DistanciaUtils.calcularDistanciaKm(
                                DistanciaUtils.LATITUDE_LOJA,
                                DistanciaUtils.LONGITUDE_LOJA,
                                cliente.getLatitude(),
                                cliente.getLongitude()
                        );

                throw new BadRequest(
                        "Endereço fora da área de entrega. Você está a "
                                + String.format("%.1f", distancia)
                                + "km da loja (limite: "
                                + (int) DistanciaUtils.RAIO_MAXIMO_KM
                                + "km)."
                );
            }
        }

        return ResponseEntity.ok(pedidoService.save(pedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {

        return ResponseEntity.ok(pedidoService.update(id, pedido));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {

        return ResponseEntity.ok(
                pedidoService.atualizarStatus(id, pedido.getStatus())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        pedidoService.delete(id);

        return ResponseEntity.noContent().build();
    }
}