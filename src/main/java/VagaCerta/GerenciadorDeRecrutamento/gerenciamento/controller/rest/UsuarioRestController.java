package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.rest;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.UsuarioCadastroDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.UsuarioDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Usuario;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/*
Restrito ao perfil de ADMIN.
Operações para consultar usuários, cadastrar, atualizar e alterar o status de uma conta.
 */

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Lista todos os usuários.
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {

        List<UsuarioDTO> usuarios = usuarioService.listarTodos()
                .stream()
                .map(this::converterParaDTO)
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    // Busca um usuário pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(
            @PathVariable UUID id) {

        return usuarioService.buscarPorId(id)
                .map(usuario ->
                        ResponseEntity.ok(converterParaDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastra um novo usuário.
    @PostMapping
    public ResponseEntity<UsuarioDTO> cadastrar(
            @RequestBody UsuarioCadastroDTO dto) {

        Usuario usuario = usuarioService.cadastrar(
                dto.nome(),
                dto.email(),
                dto.senha(),
                dto.perfil()
        );

        return ResponseEntity
                .created(URI.create("/api/usuarios/" + usuario.getId()))
                .body(converterParaDTO(usuario));
    }

    // Atualiza os dados de um usuário.
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody UsuarioCadastroDTO dto) {

        Usuario usuario = usuarioService.atualizar(
                id,
                dto.nome(),
                dto.email(),
                dto.perfil(),
                dto.senha()
        );

        return ResponseEntity.ok(converterParaDTO(usuario));
    }

    // Ativa ou desativa uma conta.
    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioDTO> alterarStatus(
            @PathVariable UUID id,
            @RequestParam boolean ativo) {

        Usuario usuario = usuarioService.alterarStatus(id, ativo);

        return ResponseEntity.ok(converterParaDTO(usuario));
    }

    // Converte Usuario para UsuarioDTO.
    private UsuarioDTO converterParaDTO(Usuario usuario) {

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.isAtivo()
        );
    }
}
