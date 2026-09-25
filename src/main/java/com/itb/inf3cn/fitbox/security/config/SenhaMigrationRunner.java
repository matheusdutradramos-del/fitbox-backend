package com.itb.inf3cn.fitbox.security.config;

import com.itb.inf3cn.fitbox.model.entity.Usuario;
import com.itb.inf3cn.fitbox.model.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// =====================================================================
// MIGRAÇÃO AUTOMÁTICA DE SENHAS ANTIGAS
// ---------------------------------------------------------------------
// Roda uma única vez, toda vez que o backend sobe. Percorre TODOS os
// usuários (Cliente, Admin, Funcionario, já que todos herdam de
// Usuario/mesma tabela) e, se encontrar alguma senha que ainda esteja
// em texto puro (cadastrada antes da gente implementar o BCrypt),
// criptografa e salva de volta.
//
// É seguro rodar isso toda vez que a aplicação iniciar: senhas que já
// estão em hash são ignoradas, então depois da primeira execução essa
// classe não faz mais nada.
// =====================================================================
@Component
public class SenhaMigrationRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public SenhaMigrationRunner(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean senhaEstaCriptografada(String senha) {

        return senha != null &&
                (senha.startsWith("$2a$") ||
                        senha.startsWith("$2b$") ||
                        senha.startsWith("$2y$"));
    }

    @Override
    public void run(String... args) {

        List<Usuario> usuarios = usuarioRepository.findAll();

        int migrados = 0;

        for (Usuario usuario : usuarios) {

            String senhaAtual = usuario.getPassword();

            boolean precisaMigrar =
                    senhaAtual != null &&
                            !senhaAtual.isBlank() &&
                            !senhaEstaCriptografada(senhaAtual);

            if (precisaMigrar) {

                usuario.setPassword(
                        passwordEncoder.encode(senhaAtual)
                );

                usuarioRepository.save(usuario);

                migrados++;
            }
        }

        if (migrados > 0) {

            System.out.println(
                    "[Migração de senha] " + migrados +
                            " senha(s) antiga(s) convertida(s) para hash BCrypt."
            );
        }
    }
}
