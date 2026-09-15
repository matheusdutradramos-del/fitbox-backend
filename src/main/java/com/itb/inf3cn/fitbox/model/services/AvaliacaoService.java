package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.BadRequest;
import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Avaliacao;
import com.itb.inf3cn.fitbox.model.entity.Cliente;
import com.itb.inf3cn.fitbox.model.entity.Pedido;
import com.itb.inf3cn.fitbox.model.entity.Produto;
import com.itb.inf3cn.fitbox.model.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public AvaliacaoService(
            AvaliacaoRepository avaliacaoRepository,
            ClienteService clienteService,
            ProdutoService produtoService,
            PedidoService pedidoService) {

        this.avaliacaoRepository = avaliacaoRepository;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    public Avaliacao findById(Long id) {

        return avaliacaoRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Avaliação não encontrada com id " + id));
    }

    public List<Avaliacao> findAll() {

        return avaliacaoRepository.findAll();
    }

    // ==========================================
    // CRIAR AVALIAÇÃO (feita pelo cliente)
    // ==========================================

    @Transactional
    public Avaliacao save(
            Integer nota,
            String comentario,
            Long clienteId,
            Long produtoId,
            Long pedidoId) {

        if (nota == null || nota < 1 || nota > 5) {

            throw new BadRequest(
                    "A nota deve ser entre 1 e 5."
            );
        }

        Cliente cliente = clienteService.findById(clienteId);
        Produto produto = produtoService.findById(produtoId);

        Pedido pedido = null;

        if (pedidoId != null) {

            pedido = pedidoService.findById(pedidoId);
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .nota(nota)
                .comentario(comentario)
                .dataAvaliacao(LocalDateTime.now())
                .respondido(false)
                .cliente(cliente)
                .produto(produto)
                .pedido(pedido)
                .codStatus(true)
                .build();

        return avaliacaoRepository.save(avaliacao);
    }

    // ==========================================
    // ADMIN RESPONDE A AVALIAÇÃO
    // ==========================================

    @Transactional
    public Avaliacao responder(
            Long id,
            String respostaAdmin) {

        Avaliacao avaliacao = findById(id);

        if (respostaAdmin == null || respostaAdmin.trim().isEmpty()) {

            throw new BadRequest(
                    "A resposta não pode ser vazia."
            );
        }

        avaliacao.setRespostaAdmin(respostaAdmin);
        avaliacao.setRespondido(true);

        return avaliacaoRepository.save(avaliacao);
    }

    // ==========================================
    // EXCLUIR AVALIAÇÃO
    // ==========================================

    @Transactional
    public void delete(Long id) {

        Avaliacao avaliacao = findById(id);

        avaliacaoRepository.delete(avaliacao);
    }
}











