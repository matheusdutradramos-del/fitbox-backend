package com.itb.inf3cn.fitbox.DTO.auth;

import com.itb.inf3cn.fitbox.DTO.auth.AuthenticationRequest;
import com.itb.inf3cn.fitbox.DTO.auth.AuthenticationResponse;
import com.itb.inf3cn.fitbox.exceptions.BadRequest;
import com.itb.inf3cn.fitbox.exceptions.Unauthorized;
import com.itb.inf3cn.fitbox.model.entity.Usuario;
import com.itb.inf3cn.fitbox.model.repository.UsuarioRepository;
import com.itb.inf3cn.fitbox.security.jwt.JwtService;
import com.itb.inf3cn.fitbox.security.token.Token;
import com.itb.inf3cn.fitbox.security.token.TokenRepository;
import com.itb.inf3cn.fitbox.security.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class AuthenticationService {

    private final UsuarioRepository repository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UsuarioRepository repository,
            TokenRepository tokenRepository,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.repository = repository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    // ==========================================
    // LOGIN PADRÃO (POST /api/v1/auth/authenticate)
    // Confere email e senha (BCrypt) pelo Spring Security
    // e devolve os tokens.
    // ==========================================

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BadRequest("Email ou Password Incorreto");
        }

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequest("Email ou Password Incorreto"));

        if (!user.isCodStatus()) {
            throw new Unauthorized("Conta inativa, por favor procurar o administrador da conta");
        }

        return gerarTokens(user);
    }


    // ==========================================
    // GERAR TOKENS PARA UM USUÁRIO JÁ AUTENTICADO
    // Usado também pelos logins de Cliente e Admin
    // (que já conferem a senha por conta própria).
    // ==========================================

    public AuthenticationResponse gerarTokens(Usuario usuario) {

        var jwtToken = jwtService.generateToken(usuario);
        var refreshToken = jwtService.generateRefreshToken(usuario);

        // só o token mais recente do usuário fica válido
        revokeAllUserTokens(usuario);
        saveUserToken(usuario, jwtToken);

        return new AuthenticationResponse(jwtToken, refreshToken);
    }


    private void saveUserToken(Usuario usuario, String jwtToken) {

        var token = new Token();
        token.setUsuario(usuario);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);

        tokenRepository.save(token);
    }


    private void revokeAllUserTokens(Usuario usuario) {

        var validUserTokens = tokenRepository.findAllValidTokenByUser(usuario.getId());

        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }


    // ==========================================
    // RENOVAR O ACCESS TOKEN (POST /api/v1/auth/refresh-token)
    // Recebe o refresh_token no header Authorization: Bearer <refresh_token>
    // ==========================================

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String refreshToken = authHeader.substring(7);

        try {

            final String userEmail = jwtService.extractUsername(refreshToken);

            if (userEmail == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            var user = this.repository.findByEmail(userEmail).orElse(null);

            if (user == null || !jwtService.isTokenValid(refreshToken, user)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            var accessToken = jwtService.generateToken(user);

            revokeAllUserTokens(user);
            saveUserToken(user, accessToken);

            response.setContentType("application/json");

            new ObjectMapper().writeValue(
                    response.getOutputStream(),
                    new AuthenticationResponse(accessToken, refreshToken)
            );

        } catch (Exception e) {
            // refresh token inválido ou expirado
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}




