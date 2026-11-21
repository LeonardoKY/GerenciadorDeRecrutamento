
package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.config;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.UsuarioDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            UsuarioDetailsService usuarioDetailsService,
            PasswordEncoder passwordEncoder) {

        this.usuarioDetailsService = usuarioDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(usuarioDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        // Recursos públicos
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**"
                        ).permitAll()

                        // Gerenciamento de usuários: somente ADMIN
                        .requestMatchers(
                                "/usuarios/**",
                                "/api/usuarios/**"
                        ).hasRole("ADMIN")

                        // Operações do sistema: ADMIN e RECRUTADOR
                        .requestMatchers(
                                "/candidatos/**",
                                "/empresas/**",
                                "/vagas/**",
                                "/processos/**",
                                "/api/candidatos/**",
                                "/api/empresas/**",
                                "/api/vagas/**",
                                "/api/processos/**"
                        ).hasAnyRole("ADMIN", "RECRUTADOR")

                        // Demais páginas exigem autenticação
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?erro")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // REST não autenticado recebe HTTP 401
                .exceptionHandling(exception -> exception
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) ->
                                        response.sendError(401),
                                PathPatternRequestMatcher.withDefaults()
                                        .matcher("/api/**")
                        )
                );

        return http.build();
    }
}
