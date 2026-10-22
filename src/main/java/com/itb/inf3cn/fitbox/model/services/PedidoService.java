package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.ItemPedido;
import com.itb.inf3cn.fitbox.model.entity.Pedido;
import com.itb.inf3cn.fitbox.model.entity.Produto;
import com.itb.inf3cn.fitbox.model.repository.PedidoRepository;
import com.itb.inf3cn.fitbox.model.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PedidoService {
    private static final Set<String> STATUS_PERMITIDOS =
            Set.of("Em preparo", "Saiu para entrega", "Finalizado", "Cancelado");
    private static final long PRAZO_PIX_MINUTOS = 15;
    private static final ZoneId FUSO_BRASIL = ZoneId.of("America/Sao_Paulo");

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    private LocalDateTime agoraBrasil() {
        return LocalDateTime.now(FUSO_BRASIL);
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
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido precisa ter pelo menos um item.");
        }

        // Sempre usar o relógio do servidor no fuso de São Paulo para novos pedidos.
        pedido.setDataHoraPedido(agoraBrasil());
        if (pedido.getStatus() == null || pedido.getStatus().isBlank()) pedido.setStatus("Em preparo");
        if (pedido.getMinutosEstimados() == null) pedido.setMinutosEstimados(45);

        Map<Long, Integer> quantidadesPorProduto = new LinkedHashMap<>();
        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new IllegalArgumentException("Cada item precisa conter o ID do produto.");
            }
            int quantidade = item.getQuantidadeItem();
            if (quantidade <= 0) {
                throw new IllegalArgumentException("A quantidade de cada produto deve ser maior que zero.");
            }
            quantidadesPorProduto.merge(item.getProduto().getId(), quantidade, Integer::sum);
        }

        Map<Long, Produto> produtosValidados = new LinkedHashMap<>();
        for (Map.Entry<Long, Integer> entrada : quantidadesPorProduto.entrySet()) {
            Produto produto = produtoRepository.findById(entrada.getKey())
                    .orElseThrow(() -> new NotFound("Produto não encontrado: " + entrada.getKey()));

            int estoque = produto.getQuantidadeEstoque() == null ? 0 : produto.getQuantidadeEstoque();
            if (estoque < entrada.getValue()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para " + produto.getNome() +
                                ". Disponíveis: " + estoque + ", solicitadas: " + entrada.getValue() + "."
                );
            }
            produtosValidados.put(entrada.getKey(), produto);
        }

        for (Map.Entry<Long, Integer> entrada : quantidadesPorProduto.entrySet()) {
            Produto produto = produtosValidados.get(entrada.getKey());
            int estoqueAtual = produto.getQuantidadeEstoque() == null ? 0 : produto.getQuantidadeEstoque();
            produto.setQuantidadeEstoque(estoqueAtual - entrada.getValue());
            produtoRepository.save(produto);
        }

        for (ItemPedido item : pedido.getItens()) {
            item.setPedido(pedido);
            item.setProduto(produtosValidados.get(item.getProduto().getId()));
            item.setCodStatus(true);
        }

        pedido.setCodStatus(true);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido update(Long id, Pedido atualizado) {
        Pedido pedido = findById(id);
        if (atualizado.getNumeroPedido() != null) pedido.setNumeroPedido(atualizado.getNumeroPedido());
        if (atualizado.getDataHoraPedido() != null) pedido.setDataHoraPedido(atualizado.getDataHoraPedido());
        if (atualizado.getDataHoraEntrega() != null) pedido.setDataHoraEntrega(atualizado.getDataHoraEntrega());
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
        if ("Cancelado".equals(pedido.getStatus())) {
            throw new IllegalArgumentException("Um pedido cancelado não pode voltar ao fluxo de entrega.");
        }
        pedido.setStatus(validarStatus(status));
        if ("Finalizado".equals(pedido.getStatus())) {
            pedido.setMinutosEstimados(0);
            pedido.setDataHoraEntrega(agoraBrasil());
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarAcompanhamento(Long id, String status, Integer minutosEstimados) {
        Pedido pedido = findById(id);
        if ("Cancelado".equals(pedido.getStatus())) {
            throw new IllegalArgumentException("Um pedido cancelado não pode ser atualizado.");
        }
        pedido.setStatus(validarStatus(status));
        if ("Finalizado".equals(pedido.getStatus())) {
            pedido.setMinutosEstimados(0);
            pedido.setDataHoraEntrega(agoraBrasil());
        } else {
            if (minutosEstimados == null || minutosEstimados < 0 || minutosEstimados > 1440) {
                throw new IllegalArgumentException("Informe uma previsão entre 0 e 1440 minutos.");
            }
            pedido.setMinutosEstimados(minutosEstimados);
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelarPixPorPrazo(Long id) {
        Pedido pedido = findById(id);

        if ("Cancelado".equals(pedido.getStatus())) return pedido;
        if ("Finalizado".equals(pedido.getStatus())) {
            throw new IllegalArgumentException("Não é possível cancelar um pedido finalizado.");
        }

        String pagamento = pedido.getPagamento() == null
                ? "" : pedido.getPagamento().trim().toLowerCase();
        if (!pagamento.equals("pix")) {
            throw new IllegalArgumentException(
                    "O cancelamento por prazo é permitido somente para pedidos via PIX."
            );
        }
        if (pedido.getDataHoraPedido() == null) {
            throw new IllegalArgumentException("O pedido não possui data de criação para validar o prazo.");
        }

        // Pedidos antigos podem ter sido gravados com o relógio do servidor em UTC.
        // Se a data estiver até 3h à frente do horário de São Paulo, normaliza esse registro legado.
        LocalDateTime agora = agoraBrasil();
        LocalDateTime dataPedido = pedido.getDataHoraPedido();
        if (dataPedido.isAfter(agora)
                && !dataPedido.isAfter(agora.plusHours(3))) {
            dataPedido = dataPedido.minusHours(3);
            pedido.setDataHoraPedido(dataPedido);
        }

        long minutosDecorridos = ChronoUnit.MINUTES.between(dataPedido, agora);
        if (minutosDecorridos < PRAZO_PIX_MINUTOS) {
            throw new IllegalArgumentException(
                    "Só é possível cancelar após 15 minutos da criação do pedido via PIX."
            );
        }

        devolverEstoque(pedido);
        pedido.setStatus("Cancelado");
        pedido.setMotivoCancelamento(
                "Pedido cancelado porque o pagamento via PIX não foi confirmado dentro do prazo de 15 minutos."
        );
        pedido.setDataHoraCancelamento(agoraBrasil());
        pedido.setMinutosEstimados(0);
        return pedidoRepository.save(pedido);
    }

    private void devolverEstoque(Pedido pedido) {
        if (pedido.getItens() == null) return;
        Map<Long, Integer> quantidades = new LinkedHashMap<>();

        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto() == null || item.getProduto().getId() == null) continue;
            int quantidade = item.getQuantidadeItem();
            if (quantidade > 0) {
                quantidades.merge(item.getProduto().getId(), quantidade, Integer::sum);
            }
        }

        for (Map.Entry<Long, Integer> entrada : quantidades.entrySet()) {
            Produto produto = produtoRepository.findById(entrada.getKey())
                    .orElseThrow(() -> new NotFound("Produto não encontrado ao devolver estoque."));
            int estoqueAtual = produto.getQuantidadeEstoque() == null ? 0 : produto.getQuantidadeEstoque();
            produto.setQuantidadeEstoque(estoqueAtual + entrada.getValue());
            produtoRepository.save(produto);
        }
    }

    private String validarStatus(String status) {
        if (status == null || !STATUS_PERMITIDOS.contains(status)) {
            throw new IllegalArgumentException(
                    "Status inválido. Use: Em preparo, Saiu para entrega, Finalizado ou Cancelado."
            );
        }
        return status;
    }

    @Transactional
    public void delete(Long id) {
        pedidoRepository.delete(findById(id));
    }
}
