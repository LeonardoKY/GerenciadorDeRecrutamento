package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.web;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.UsuarioCadastroDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.UsuarioDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Perfil;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Usuario;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.UsuarioService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/*
Responsável pelas páginas de gerenciamento.
Restrito ao perfil de ADMIN.
O ADMIN poderá listar usuários, cadastrar, editar e ativar ou desativar contas.
 */

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Lista todos os usuários cadastrados.
    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "usuarios",
                usuarioService.listarTodos()
                        .stream()
                        .map(this::converterParaDTO)
                        .toList()
        );

        return "usuarios/lista";
    }

    // Exibe o formulário de cadastro.
    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute("usuario", new UsuarioCadastroDTO(
                "", "", "", null
        ));

        model.addAttribute("perfis", Perfil.values());

        return "usuarios/formulario";
    }

    // Cadastra um novo usuário.
    @PostMapping
    public String cadastrar(
            @ModelAttribute UsuarioCadastroDTO dto,
            RedirectAttributes redirectAttributes) {

        usuarioService.cadastrar(
                dto.nome(),
                dto.email(),
                dto.senha(),
                dto.perfil()
        );

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Usuário cadastrado com sucesso."
        );

        return "redirect:/usuarios";
    }

    // Exibe o formulário de edição.
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable UUID id,
            Model model) {

        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."
                        ));

        UsuarioCadastroDTO dto = new UsuarioCadastroDTO(
                usuario.getNome(),
                usuario.getEmail(),
                "",
                usuario.getPerfil()
        );

        model.addAttribute("usuario", dto);
        model.addAttribute("usuarioId", id);
        model.addAttribute("perfis", Perfil.values());

        return "usuarios/formulario";
    }

    // Atualiza os dados do usuário.
    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @ModelAttribute UsuarioCadastroDTO dto,
            RedirectAttributes redirectAttributes) {

        usuarioService.atualizar(
                id,
                dto.nome(),
                dto.email(),
                dto.perfil(),
                dto.senha()
        );

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Usuário atualizado com sucesso."
        );

        return "redirect:/usuarios";
    }

    // Ativa ou desativa uma conta.
    @PostMapping("/{id}/status")
    public String alterarStatus(
            @PathVariable UUID id,
            @RequestParam boolean ativo,
            RedirectAttributes redirectAttributes) {

        usuarioService.alterarStatus(id, ativo);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                ativo
                        ? "Usuário ativado com sucesso."
                        : "Usuário desativado com sucesso."
        );

        return "redirect:/usuarios";
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
