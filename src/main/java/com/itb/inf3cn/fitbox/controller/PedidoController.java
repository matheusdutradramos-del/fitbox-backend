
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
import java.util.Locale;

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
    public ResponseEntity<Pedido> salvar(
            @RequestBody Pedido pedido) {

        /*
         * VALIDACAO DA AREA DE ENTREGA
         *
         * A distancia e calculada no servidor usando
         * as coordenadas cadastradas para o cliente.
         */
        if (pedido.getCliente() != null
                && pedido.getCliente().getId() != null) {

            Cliente cliente = clienteService.findById(
                    pedido.getCliente().getId()
            );

            Double latitude = cliente.getLatitude();
            Double longitude = cliente.getLongitude();

            if (latitude == null || longitude == null) {
                throw new BadRequest(
                        "Nao foi possivel confirmar sua localizacao. "
                                + "Atualize seu endereco em 'Meu Endereco'."
                );
            }

            if (!DistanciaUtils.coordenadasValidas(
                    latitude, longitude)) {

                throw new BadRequest(
                        "As coordenadas do endereco sao invalidas. "
                                + "Confira o CEP e salve seu endereco novamente."
                );
            }

            double distancia =
                    DistanciaUtils.calcularDistanciaDaLojaKm(
                            latitude,
                            longitude
                    );

            if (distancia > DistanciaUtils.RAIO_MAXIMO_KM) {

                String distanciaFormatada = String.format(
                        Locale.forLanguageTag("pt-BR"),
                        "%.1f",
                        distancia
                );

                throw new BadRequest(
                        "Endereco fora da area de entrega. "
                                + "A distancia em linha reta ate a loja e de "
                                + distanciaFormatada
                                + " km. O limite de entrega e de "
                                + (int) DistanciaUtils.RAIO_MAXIMO_KM
                                + " km."
                );
            }
        }

        return ResponseEntity.ok(pedidoService.save(pedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {

        return ResponseEntity.ok(
                pedidoService.update(id, pedido)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {

        return ResponseEntity.ok(
                pedidoService.atualizarStatus(
                        id,
                        pedido.getStatus()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        pedidoService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
