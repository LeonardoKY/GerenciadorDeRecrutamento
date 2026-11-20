package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.web;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Genero;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.NivelFormacao;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.CandidatoService;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.EnderecoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;
    private final EnderecoService enderecoService;

    public CandidatoController(
            CandidatoService candidatoService,
            EnderecoService enderecoService) {

        this.candidatoService = candidatoService;
        this.enderecoService = enderecoService;
    }

    // Lista e pesquisa candidatos.
    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String formacao,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado,
            Model model) {

        model.addAttribute("candidatos",
                candidatoService.buscar(
                        nome, cpf, email, formacao, cidade, estado));

        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("email", email);
        model.addAttribute("formacao", formacao);
        model.addAttribute("cidade", cidade);
        model.addAttribute("estado", estado);

        return "candidatos/lista";
    }

    // Exibe o formulário de cadastro.
    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute("candidato", new Candidato());
        model.addAttribute("endereco", new Endereco());

        adicionarOpcoes(model);

        return "candidatos/formulario";
    }

    // Cadastra um candidato.
    @PostMapping
    public String cadastrar(
            @ModelAttribute Candidato candidato,
            @ModelAttribute Endereco endereco,
            RedirectAttributes redirectAttributes) {

        Candidato salvo = candidatoService.salvar(candidato);

        if (enderecoInformado(endereco)) {
            enderecoService.salvarParaCandidato(
                    salvo.getId(), endereco);
        }

        redirectAttributes.addFlashAttribute(
                "sucesso", "Candidato cadastrado com sucesso.");

        return "redirect:/candidatos/" + salvo.getId();
    }

    // Visualiza os dados de um candidato.
    @GetMapping("/{id}")
    public String visualizar(
            @PathVariable UUID id,
            Model model) {

        Candidato candidato = candidatoService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Candidato não encontrado."));

        model.addAttribute("candidato", candidato);

        model.addAttribute(
                "endereco",
                enderecoService.buscarPorCandidato(id).orElse(null));

        return "candidatos/detalhes";
    }

    // Exibe o formulário de edição.
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable UUID id,
            Model model) {

        Candidato candidato = candidatoService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Candidato não encontrado."));

        model.addAttribute("candidato", candidato);

        model.addAttribute(
                "endereco",
                enderecoService.buscarPorCandidato(id)
                        .orElse(new Endereco()));

        adicionarOpcoes(model);

        return "candidatos/formulario";
    }

    // Atualiza os dados do candidato e seu endereço.
    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @ModelAttribute Candidato candidato,
            @ModelAttribute Endereco endereco,
            RedirectAttributes redirectAttributes) {

        candidatoService.atualizar(id, candidato);

        if (enderecoInformado(endereco)) {

            Endereco enderecoAtual = enderecoService
                    .buscarPorCandidato(id)
                    .orElse(null);

            if (enderecoAtual == null) {
                enderecoService.salvarParaCandidato(
                        id, endereco);
            } else {
                enderecoService.atualizar(
                        enderecoAtual.getId(), endereco);
            }
        }

        redirectAttributes.addFlashAttribute(
                "sucesso", "Candidato atualizado com sucesso.");

        return "redirect:/candidatos/" + id;
    }

    // Exclui um candidato.
    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        candidatoService.excluir(id);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Candidato excluído com sucesso.");

        return "redirect:/candidatos";
    }

    // Exclui somente o endereço do candidato.
    @PostMapping("/{id}/endereco/excluir")
    public String excluirEndereco(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        candidatoService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Candidato não encontrado."));

        enderecoService.buscarPorCandidato(id)
                .ifPresent(endereco ->
                        enderecoService.excluir(endereco.getId()));

        redirectAttributes.addFlashAttribute(
                "sucesso", "Endereço excluído com sucesso.");

        return "redirect:/candidatos/" + id;
    }

    // Verifica se o formulário contém dados de endereço.
    private boolean enderecoInformado(Endereco endereco) {
        return endereco != null &&
                endereco.getCep() != null &&
                !endereco.getCep().isBlank();
    }

    // Disponibiliza os valores dos enumeradores no formulário.
    private void adicionarOpcoes(Model model) {
        model.addAttribute("generos", Genero.values());
        model.addAttribute("formacoes", NivelFormacao.values());
    }
}
