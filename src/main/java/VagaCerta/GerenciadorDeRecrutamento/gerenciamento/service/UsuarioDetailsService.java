package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Usuario;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

//Integra a entidade Usuario ao mecanismo de autenticação do Sring security

@Service
public class UsuarioDetailsService implements UserDetailsService {// Interface do Spring Security (define como buscar os usuários durante a autenticação).

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) // o identificador é o email.
            throws UsernameNotFoundException {

        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException(
                    "E-mail não informado.");
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);

        // Recuperação do usuário:
        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado."));

        // Converte a entidade Usuario para o objeto que o Spring Security utiliza na autenticação.
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getPerfil().name()) // ".roles()" adiciona automaticamente o prefixo ROLE_
                .disabled(!usuario.isAtivo()) // Impede a autenticação de usuários desativados.
                .build();
    }
}
/*
Obs.: autoridade (permissão) no Spring Security:
Admin = ROLE_ADMIN
Recrutador = ROLE_RECRUTADOR
 */