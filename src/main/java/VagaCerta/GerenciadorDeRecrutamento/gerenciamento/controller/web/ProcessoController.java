package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.web;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Processo;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.StatusProcesso;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.ProcessoService;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.CandidatoService;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.VagaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/processos")
public class ProcessoController {

    private final ProcessoService processoService;
    private final CandidatoService candidatoService;
    private final VagaService vagaService;

    public ProcessoController(
            ProcessoService processoService,
            CandidatoService candidatoService,
            VagaService vagaService) {

        this.processoService = processoService;
        this.candidatoService = candidatoService;
        this.vagaService = vagaService;
    }

    // Lista todos os processos seletivos.
    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "processos", processoService.listarTodos());

        return "processos/lista";
    }

    // Visualiza um processo seletivo.
    @GetMapping("/{id}")
    public String visualizar(
            @PathVariable UUID id,
            Model model) {

        Processo processo = processoService.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Processo não encontrado."));

        model.addAttribute("processo", processo);
        model.addAttribute("statusProcessos", StatusProcesso.values());

        return "processos/detalhes";
    }

    // Lista processos de um candidato.
    @GetMapping("/candidato/{candidatoId}")
    public String buscarPorCandidato(
            @PathVariable UUID candidatoId,
            Model model) {

        model.addAttribute(
                "processos",
                processoService.buscarPorCandidato(candidatoId));

        model.addAttribute("candidatoId", candidatoId);

        return "processos/lista";
    }

    // Lista processos de uma vaga.
    @GetMapping("/vaga/{vagaId}")
    public String buscarPorVaga(
            @PathVariable UUID vagaId,
            Model model) {

        model.addAttribute(
                "processos",
                processoService.buscarPorVaga(vagaId));

        model.addAttribute("vagaId", vagaId);

        return "processos/lista";
    }

    // Exibe o formulário de inscrição.
    @GetMapping("/nova-inscricao")
    public String novaInscricao(Model model) {

        model.addAttribute(
                "candidatos", candidatoService.listarTodos());

        model.addAttribute(
                "vagas", vagaService.listarTodas());

        return "processos/inscricao-formulario";
    }

    // Realiza a inscrição de um candidato em uma vaga.
    @PostMapping("/inscrever")
    public String inscrever(
            @RequestParam UUID candidatoId,
            @RequestParam UUID vagaId,
            @RequestParam double pretensaoSalarial,
            RedirectAttributes redirectAttributes) {

        Processo processo = processoService.inscrever(
                candidatoId,
                vagaId,
                pretensaoSalarial);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Inscrição realizada com sucesso.");

        return "redirect:/processos/" + processo.getId();
    }

    // Atualiza o status de um processo seletivo.
    @PostMapping("/{id}/status")
    public String atualizarStatus(
            @PathVariable UUID id,
            @RequestParam StatusProcesso status,
            RedirectAttributes redirectAttributes) {

        processoService.atualizarStatus(id, status);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Status atualizado com sucesso.");

        return "redirect:/processos/" + id;
    }
}
