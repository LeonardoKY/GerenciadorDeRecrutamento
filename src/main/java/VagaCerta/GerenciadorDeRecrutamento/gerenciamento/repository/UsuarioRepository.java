package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository
        extends JpaRepository<Usuario, UUID> {

    // Busca usuário pelo e-mail utilizado no login.
    Optional<Usuario> findByEmail(String email);

    // Verifica se o e-mail já está cadastrado.
    boolean existsByEmail(String email);
}
