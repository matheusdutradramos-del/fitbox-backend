package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Pedido;
import com.itb.inf3cn.fitbox.model.repository.PedidoRepository;
import com.itb.inf3cn.fitbox.model.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PedidoService {
    private static final Set<String> STATUS_PERMITIDOS =
            Set.of("Em preparo", "Saiu para entrega", "Finalizado");

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Pedido não encontrado com id " + id));
    }

    @Transactional
    public Pedido save(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            throw new IllegalArgumentException("O pedido precisa estar vinculado a um cliente cadastrado.");
        }
        if (pedido.getDataHoraPedido() == null) pedido.setDataHoraPedido(LocalDateTime.now());
        if (pedido.getStatus() == null || pedido.getStatus().isBlank()) pedido.setStatus("Em preparo");
        if (pedido.getMinutosEstimados() == null) pedido.setMinutosEstimados(30);
        if (pedido.getItens() != null) {
            pedido.getItens().forEach(item -> {
                item.setPedido(pedido);
                if (item.getProduto() == null || item.getProduto().getId() == null) {
                    throw new IllegalArgumentException("Cada item precisa conter o ID do produto.");
                }
                item.setProduto(produtoRepository.findById(item.getProduto().getId())
                        .orElseThrow(() -> new NotFound("Produto não encontrado.")));
                item.setCodStatus(true);
            });
        }
        pedido.setCodStatus(true);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido update(Long id, Pedido atualizado) {
        Pedido pedido = findById(id);
        if (atualizado.getNumeroPedido() != null) pedido.setNumeroPedido(atualizado.getNumeroPedido());
        if (atualizado.getDataHoraPedido() != null) pedido.setDataHoraPedido(atualizado.getDataHoraPedido());
        pedido.setDataHoraEntrega(atualizado.getDataHoraEntrega());
        if (atualizado.getValorTotal() != null) pedido.setValorTotal(atualizado.getValorTotal());
        if (atualizado.getStatus() != null) pedido.setStatus(validarStatus(atualizado.getStatus()));
        if (atualizado.getPagamento() != null) pedido.setPagamento(atualizado.getPagamento());
        if (atualizado.getEnderecoEntrega() != null) pedido.setEnderecoEntrega(atualizado.getEnderecoEntrega());
        if (atualizado.getMinutosEstimados() != null) pedido.setMinutosEstimados(atualizado.getMinutosEstimados());
        pedido.setCodStatus(atualizado.isCodStatus());
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarStatus(Long id, String status) {
        Pedido pedido = findById(id);
        pedido.setStatus(validarStatus(status));
        if ("Finalizado".equals(pedido.getStatus())) {
            pedido.setMinutosEstimados(0);
            pedido.setDataHoraEntrega(LocalDateTime.now());
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarAcompanhamento(Long id, String status, Integer minutosEstimados) {
        Pedido pedido = findById(id);
        pedido.setStatus(validarStatus(status));
        if ("Finalizado".equals(pedido.getStatus())) {
            pedido.setMinutosEstimados(0);
            pedido.setDataHoraEntrega(LocalDateTime.now());
        } else {
            if (minutosEstimados == null || minutosEstimados < 0 || minutosEstimados > 1440) {
                throw new IllegalArgumentException("Informe uma previsão entre 0 e 1440 minutos.");
            }
            pedido.setMinutosEstimados(minutosEstimados);
        }
        return pedidoRepository.save(pedido);
    }

    private String validarStatus(String status) {
        if (status == null || !STATUS_PERMITIDOS.contains(status)) {
            throw new IllegalArgumentException("Status inválido. Use: Em preparo, Saiu para entrega ou Finalizado.");
        }
        return status;
    }

    @Transactional
    public void delete(Long id) {
        pedidoRepository.delete(findById(id));
    }
}
