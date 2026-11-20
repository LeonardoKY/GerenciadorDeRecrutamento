package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.web;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Vaga;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Empresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.AreaAtuacao;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Modalidade;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Senioridade;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.StatusVaga;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.VagaService;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.EmpresaService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService vagaService;
    private final EmpresaService empresaService;

    public VagaController(
            VagaService vagaService,
            EmpresaService empresaService) {

        this.vagaService = vagaService;
        this.empresaService = empresaService;
    }

    // Lista e pesquisa vagas utilizando filtros.
    @GetMapping
    public String listar(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate aberturaInicial,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate aberturaFinal,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechamentoInicial,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechamentoFinal,

            @RequestParam(required = false)
            Modalidade modalidade,

            @RequestParam(required = false)
            Senioridade senioridade,

            @RequestParam(required = false)
            AreaAtuacao area,

            @RequestParam(required = false)
            StatusVaga status,

            Model model) {

        model.addAttribute("vagas",
                vagaService.buscar(
                        aberturaInicial,
                        aberturaFinal,
                        fechamentoInicial,
                        fechamentoFinal,
                        modalidade,
                        senioridade,
                        area,
                        status
                ));

        // Mantém os filtros selecionados na página.
        model.addAttribute("aberturaInicial", aberturaInicial);
        model.addAttribute("aberturaFinal", aberturaFinal);
        model.addAttribute("fechamentoInicial", fechamentoInicial);
        model.addAttribute("fechamentoFinal", fechamentoFinal);
        model.addAttribute("modalidadeSelecionada", modalidade);
        model.addAttribute("senioridadeSelecionada", senioridade);
        model.addAttribute("areaSelecionada", area);
        model.addAttribute("statusSelecionado", status);

        adicionarOpcoes(model);

        return "vagas/lista";
    }

    // Exibe o formulário de cadastro.
    @GetMapping("/nova")
    public String nova(Model model) {

        model.addAttribute("vaga", new Vaga());
        model.addAttribute("empresas", empresaService.listarTodas());

        adicionarOpcoes(model);

        return "vagas/formulario";
    }

    // Cadastra uma nova vaga.
    @PostMapping
    public String cadastrar(
            @ModelAttribute Vaga vaga,
            @RequestParam UUID empresaId,
            RedirectAttributes redirectAttributes) {

        Empresa empresa = empresaService.buscarPorId(empresaId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Empresa não encontrada."));

        vaga.setEmpresa(empresa);

        Vaga salva = vagaService.salvar(vaga);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Vaga cadastrada com sucesso.");

        return "redirect:/vagas/" + salva.getId();
    }

    // Visualiza uma vaga.
    @GetMapping("/{id}")
    public String visualizar(
            @PathVariable UUID id,
            Model model) {

        Vaga vaga = vagaService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Vaga não encontrada."));

        model.addAttribute("vaga", vaga);

        return "vagas/detalhes";
    }

    // Exibe o formulário de edição.
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable UUID id,
            Model model) {

        Vaga vaga = vagaService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Vaga não encontrada."));

        model.addAttribute("vaga", vaga);
        model.addAttribute("empresas", empresaService.listarTodas());

        adicionarOpcoes(model);

        return "vagas/formulario";
    }

    // Atualiza uma vaga.
    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @ModelAttribute Vaga vaga,
            RedirectAttributes redirectAttributes) {

        vagaService.atualizar(id, vaga);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Vaga atualizada com sucesso.");

        return "redirect:/vagas/" + id;
    }

    // Exclui uma vaga.
    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        vagaService.excluir(id);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Vaga excluída com sucesso.");

        return "redirect:/vagas";
    }

    // Disponibiliza os enumeradores para os formulários.
    private void adicionarOpcoes(Model model) {

        model.addAttribute("modalidades", Modalidade.values());
        model.addAttribute("senioridades", Senioridade.values());
        model.addAttribute("areas", AreaAtuacao.values());
        model.addAttribute("statusVagas", StatusVaga.values());
    }
}
