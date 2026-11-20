package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.web;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Empresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.PorteEmpresa;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.EmpresaService;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.EnderecoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final EnderecoService enderecoService;

    public EmpresaController(
            EmpresaService empresaService,
            EnderecoService enderecoService) {

        this.empresaService = empresaService;
        this.enderecoService = enderecoService;
    }

    // Lista e pesquisa empresas.
    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) PorteEmpresa porte,
            Model model) {

        if (nome != null && !nome.isBlank()) {
            model.addAttribute(
                    "empresas", empresaService.buscarPorNome(nome));

        } else if (porte != null) {
            model.addAttribute(
                    "empresas", empresaService.buscarPorPorte(porte));

        } else {
            model.addAttribute(
                    "empresas", empresaService.listarTodas());
        }

        model.addAttribute("nome", nome);
        model.addAttribute("porteSelecionado", porte);
        model.addAttribute("portes", PorteEmpresa.values());

        return "empresas/lista";
    }

    // Exibe o formulário de cadastro.
    @GetMapping("/nova")
    public String nova(Model model) {

        model.addAttribute("empresa", new Empresa());
        adicionarOpcoes(model);

        return "empresas/formulario";
    }

    // Cadastra uma empresa.
    @PostMapping
    public String cadastrar(
            @ModelAttribute Empresa empresa,
            RedirectAttributes redirectAttributes) {

        Empresa salva = empresaService.salvar(empresa);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Empresa cadastrada com sucesso.");

        return "redirect:/empresas/" + salva.getId();
    }

    // Visualiza uma empresa e seus endereços.
    @GetMapping("/{id}")
    public String visualizar(
            @PathVariable UUID id,
            Model model) {

        Empresa empresa = empresaService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Empresa não encontrada."));

        model.addAttribute("empresa", empresa);

        model.addAttribute(
                "enderecos",
                enderecoService.listarPorEmpresa(id));

        return "empresas/detalhes";
    }

    // Exibe o formulário de edição.
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable UUID id,
            Model model) {

        Empresa empresa = empresaService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Empresa não encontrada."));

        model.addAttribute("empresa", empresa);
        adicionarOpcoes(model);

        return "empresas/formulario";
    }

    // Atualiza os dados da empresa.
    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @ModelAttribute Empresa empresa,
            RedirectAttributes redirectAttributes) {

        empresaService.atualizar(id, empresa);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Empresa atualizada com sucesso.");

        return "redirect:/empresas/" + id;
    }

    // Exclui uma empresa.
    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        empresaService.excluir(id);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Empresa excluída com sucesso.");

        return "redirect:/empresas";
    }

    // Exibe o formulário de novo endereço.
    @GetMapping("/{id}/enderecos/novo")
    public String novoEndereco(
            @PathVariable UUID id,
            Model model) {

        empresaService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Empresa não encontrada."));

        model.addAttribute("empresaId", id);
        model.addAttribute("endereco", new Endereco());

        return "empresas/endereco-formulario";
    }

    // Adiciona um endereço à empresa.
    @PostMapping("/{id}/enderecos")
    public String adicionarEndereco(
            @PathVariable UUID id,
            @ModelAttribute Endereco endereco,
            RedirectAttributes redirectAttributes) {

        enderecoService.salvarParaEmpresa(id, endereco);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Endereço adicionado com sucesso.");

        return "redirect:/empresas/" + id;
    }

    // Exibe o formulário de edição de endereço.
    @GetMapping("/{id}/enderecos/{enderecoId}/editar")
    public String editarEndereco(
            @PathVariable UUID id,
            @PathVariable UUID enderecoId,
            Model model) {

        Endereco endereco =
                buscarEnderecoDaEmpresa(id, enderecoId);

        model.addAttribute("empresaId", id);
        model.addAttribute("endereco", endereco);

        return "empresas/endereco-formulario";
    }

    // Atualiza um endereço.
    @PostMapping("/{id}/enderecos/{enderecoId}")
    public String atualizarEndereco(
            @PathVariable UUID id,
            @PathVariable UUID enderecoId,
            @ModelAttribute Endereco endereco,
            RedirectAttributes redirectAttributes) {

        // Verifica se o endereço pertence à empresa.
        buscarEnderecoDaEmpresa(id, enderecoId);

        enderecoService.atualizar(enderecoId, endereco);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Endereço atualizado com sucesso.");

        return "redirect:/empresas/" + id;
    }

    // Exclui um endereço.
    @PostMapping("/{id}/enderecos/{enderecoId}/excluir")
    public String excluirEndereco(
            @PathVariable UUID id,
            @PathVariable UUID enderecoId,
            RedirectAttributes redirectAttributes) {

        buscarEnderecoDaEmpresa(id, enderecoId);

        enderecoService.excluir(enderecoId);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Endereço excluído com sucesso.");

        return "redirect:/empresas/" + id;
    }

    // Busca um endereço verificando se pertence à empresa.
    private Endereco buscarEnderecoDaEmpresa(
            UUID empresaId,
            UUID enderecoId) {

        empresaService.buscarPorId(empresaId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Empresa não encontrada."));

        return enderecoService.listarPorEmpresa(empresaId)
                .stream()
                .filter(endereco ->
                        endereco.getId().equals(enderecoId))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Endereço não encontrado para esta empresa."));
    }

    // Disponibiliza os portes de empresa no formulário.
    private void adicionarOpcoes(Model model) {
        model.addAttribute("portes", PorteEmpresa.values());
    }
}
