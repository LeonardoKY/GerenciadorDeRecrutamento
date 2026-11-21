package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Perfil;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Usuario;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lista todos os usuários cadastrados.
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Busca um usuário pelo ID.
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    // Busca um usuário pelo e-mail.
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(normalizarEmail(email));
    }

    // Cadastra um novo usuário.
    @Transactional
    public Usuario cadastrar(
            String nome,
            String email,
            String senha,
            Perfil perfil) {

        validarDados(nome, email, senha, perfil);

        String emailNormalizado = normalizarEmail(email);

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse e-mail.");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(nome.trim());
        usuario.setEmail(emailNormalizado);

        // A senha original nunca é armazenada.
        usuario.setSenha(passwordEncoder.encode(senha));

        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    // Atualiza os dados de um usuário.
    @Transactional
    public Usuario atualizar(
            UUID id,
            String nome,
            String email,
            Perfil perfil,
            String novaSenha) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."));

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do usuário é obrigatório.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "O e-mail do usuário é obrigatório.");
        }

        if (perfil == null) {
            throw new IllegalArgumentException(
                    "O perfil do usuário é obrigatório.");
        }

        String emailNormalizado = normalizarEmail(email);

        Optional<Usuario> usuarioExistente =
                usuarioRepository.findByEmail(emailNormalizado);

        if (usuarioExistente.isPresent()
                && !usuarioExistente.get().getId().equals(id)) {

            throw new IllegalArgumentException(
                    "Já existe outro usuário cadastrado com esse e-mail.");
        }

        usuario.setNome(nome.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setPerfil(perfil);

        // Só altera a senha quando uma nova senha for informada.
        if (novaSenha != null && !novaSenha.isBlank()) {

            if (novaSenha.length() < 8) {
                throw new IllegalArgumentException(
                        "A senha deve possuir pelo menos 8 caracteres.");
            }

            usuario.setSenha(passwordEncoder.encode(novaSenha));
        }

        return usuarioRepository.save(usuario);
    }

    // Ativa ou desativa uma conta.
    @Transactional
    public Usuario alterarStatus(UUID id, boolean ativo) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."));

        // Impede a desativação do último administrador ativo.
        if (!ativo
                && usuario.getPerfil() == Perfil.ADMIN
                && usuario.isAtivo()
                && !existeOutroAdminAtivo(id)) {

            throw new IllegalStateException(
                    "Não é possível desativar o último administrador ativo.");
        }

        usuario.setAtivo(ativo);

        return usuarioRepository.save(usuario);
    }

    // Verifica se existe outro administrador ativo.
    private boolean existeOutroAdminAtivo(UUID idIgnorado) {

        return usuarioRepository.findAll().stream()
                .anyMatch(usuario ->
                        !usuario.getId().equals(idIgnorado)
                                && usuario.getPerfil() == Perfil.ADMIN
                                && usuario.isAtivo());
    }

    // Valida os dados necessários para o cadastro.
    private void validarDados(
            String nome,
            String email,
            String senha,
            Perfil perfil) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do usuário é obrigatório.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "O e-mail do usuário é obrigatório.");
        }

        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve possuir pelo menos 8 caracteres.");
        }

        if (perfil == null) {
            throw new IllegalArgumentException(
                    "O perfil do usuário é obrigatório.");
        }
    }

    // Padroniza os e-mails para evitar duplicidades por maiúsculas.
    private String normalizarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "O e-mail é obrigatório.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
