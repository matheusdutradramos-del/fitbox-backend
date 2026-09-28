package com.itb.inf3cn.fitbox.security.config;

import com.itb.inf3cn.fitbox.security.exceptions.CustomAccessDeniedHandler;
import com.itb.inf3cn.fitbox.security.exceptions.CustomAuthenticationEntryPoint;
import com.itb.inf3cn.fitbox.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
            "/api/v1/index",
            "/api/v2/api-docs",
            "/images/**"
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider,
            LogoutHandler logoutHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.logoutHandler = logoutHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())

                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(req -> req

                        // ---------- PÚBLICO (não precisa de token) ----------
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/clientes/login",
                                "/api/v1/admins/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/clientes").permitAll() // cadastro
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/produtos", "/api/v1/produtos/**",
                                "/api/v1/categorias", "/api/v1/categorias/**").permitAll() // catálogo

                        // ---------- SÓ ADMIN / FUNCIONÁRIO ----------
                        .requestMatchers("/api/v1/produtos/**", "/api/v1/categorias/**")
                        .hasAnyRole("ADMIN", "FUNCIONARIO") // criar, editar, excluir
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/avaliacoes/**")
                        .hasAnyRole("ADMIN", "FUNCIONARIO") // responder avaliação
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/avaliacoes/**")
                        .hasAnyRole("ADMIN", "FUNCIONARIO") // excluir avaliação
                        .requestMatchers(HttpMethod.GET, "/api/v1/clientes")
                        .hasAnyRole("ADMIN", "FUNCIONARIO") // listar todos os clientes

                        // ---------- SÓ ADMIN ----------
                        .requestMatchers("/api/v1/admins/**", "/api/v1/funcionarios/**")
                        .hasRole("ADMIN")

                        // ---------- QUALQUER OUTRA ROTA: PRECISA ESTAR LOGADO ----------
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)   // 401
                        .accessDeniedHandler(accessDeniedHandler)             // 403
                )

                // sem sessão no servidor: cada requisição se identifica pelo token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutUrl("/api/v1/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) ->
                                SecurityContextHolder.clearContext())
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
