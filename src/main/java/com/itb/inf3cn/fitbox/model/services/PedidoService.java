package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Pedido;
import com.itb.inf3cn.fitbox.model.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
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
    public void delete(Long id) {

        Pedido pedido = findById(id);

        pedidoRepository.delete(pedido);
    }
}