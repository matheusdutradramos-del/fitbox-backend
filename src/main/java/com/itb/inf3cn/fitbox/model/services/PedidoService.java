package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Pedido;
import com.itb.inf3cn.fitbox.model.repository.PedidoRepository;
import com.itb.inf3cn.fitbox.model.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Pedido não encontrado com id " + id));
    }

    @Transactional
    public Pedido save(Pedido pedido) {

        if (pedido.getItens() != null) {

            pedido.getItens().forEach(item -> {

                item.setPedido(pedido);

                item.setProduto(
                        produtoRepository.findById(item.getProduto().getId())
                                .orElseThrow(() ->
                                        new NotFound("Produto não encontrado.")
                                )
                );

            });

        }

        return pedidoRepository.save(pedido);

    }

    @Transactional
    public Pedido update(Long id, Pedido pedidoAtualizado) {

        Pedido pedido = findById(id);

        pedido.setNumeroPedido(pedidoAtualizado.getNumeroPedido());
        pedido.setDataHoraPedido(pedidoAtualizado.getDataHoraPedido());
        pedido.setDataHoraEntrega(pedidoAtualizado.getDataHoraEntrega());
        pedido.setValorTotal(pedidoAtualizado.getValorTotal());
        pedido.setStatus(pedidoAtualizado.getStatus());
        pedido.setCliente(pedidoAtualizado.getCliente());
        pedido.setCodStatus(pedidoAtualizado.isCodStatus());

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarStatus(Long id, String status) {

        Pedido pedido = findById(id);

        pedido.setStatus(status);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void delete(Long id) {

        Pedido pedido = findById(id);

        pedidoRepository.delete(pedido);
    }
}