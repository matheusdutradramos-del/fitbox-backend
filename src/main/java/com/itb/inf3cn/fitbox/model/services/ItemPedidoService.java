package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.ItemPedido;
import com.itb.inf3cn.fitbox.model.repository.ItemPedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public List<ItemPedido> findAll() {
        return itemPedidoRepository.findAll();
    }

    public ItemPedido findById(Long id) {
        return itemPedidoRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Item do pedido não encontrado com id " + id));
    }

    @Transactional
    public ItemPedido save(ItemPedido itemPedido) {
        return itemPedidoRepository.save(itemPedido);
    }

    @Transactional
    public ItemPedido update(Long id, ItemPedido itemPedidoAtualizado) {

        ItemPedido itemPedido = findById(id);

        itemPedido.setQuantidadeItem(itemPedidoAtualizado.getQuantidadeItem());
        itemPedido.setValorUnitario(itemPedidoAtualizado.getValorUnitario());
        itemPedido.setProduto(itemPedidoAtualizado.getProduto());
        itemPedido.setPedido(itemPedidoAtualizado.getPedido());
        itemPedido.setCodStatus(itemPedidoAtualizado.isCodStatus());

        return itemPedidoRepository.save(itemPedido);
    }

    @Transactional
    public void delete(Long id) {

        ItemPedido itemPedido = findById(id);

        itemPedidoRepository.delete(itemPedido);
    }
}