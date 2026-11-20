package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.rest;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.ProcessoDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Processo;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.StatusProcesso;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.ProcessoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/processos")
public class ProcessoRestController {

    private final ProcessoService processoService;

    public ProcessoRestController(ProcessoService processoService) {
        this.processoService = processoService;
    }

    // Lista todos os processos seletivos.
    @GetMapping
    public ResponseEntity<List<ProcessoDTO>> listarTodos() {

        List<ProcessoDTO> processos = processoService.listarTodos()
                .stream()
                .map(this::converterParaDTO)
                .toList();

        return ResponseEntity.ok(processos);
    }

    // Busca um processo pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<ProcessoDTO> buscarPorId(
            @PathVariable UUID id) {

        return processoService.buscarPorId(id)
                .map(this::converterParaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lista os processos de um candidato.
    @GetMapping("/candidato/{candidatoId}")
    public ResponseEntity<List<ProcessoDTO>> buscarPorCandidato(
            @PathVariable UUID candidatoId) {

        List<ProcessoDTO> processos =
                processoService.buscarPorCandidato(candidatoId)
                        .stream()
                        .map(this::converterParaDTO)
                        .toList();

        return ResponseEntity.ok(processos);
    }

    // Lista os processos de uma vaga.
    @GetMapping("/vaga/{vagaId}")
    public ResponseEntity<List<ProcessoDTO>> buscarPorVaga(
            @PathVariable UUID vagaId) {

        List<ProcessoDTO> processos =
                processoService.buscarPorVaga(vagaId)
                        .stream()
                        .map(this::converterParaDTO)
                        .toList();

        return ResponseEntity.ok(processos);
    }

    // Realiza a inscrição de um candidato em uma vaga.
    @PostMapping
    public ResponseEntity<ProcessoDTO> inscrever(
            @RequestBody ProcessoDTO dto) {

        Processo processo = processoService.inscrever(
                dto.candidatoId(),
                dto.vagaId(),
                dto.pretensaoSalarial()
        );

        return ResponseEntity
                .created(URI.create("/api/processos/" + processo.getId()))
                .body(converterParaDTO(processo));
    }

    // Atualiza o status de um processo seletivo.
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProcessoDTO> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody ProcessoDTO dto) {

        StatusProcesso novoStatus = dto.status();

        Processo processo =
                processoService.atualizarStatus(id, novoStatus);

        return ResponseEntity.ok(converterParaDTO(processo));
    }

    // Converte Processo para ProcessoDTO.
    private ProcessoDTO converterParaDTO(Processo processo) {

        return new ProcessoDTO(
                processo.getId(),
                processo.getPretensaoSalarial(),
                processo.getInscricao(),
                processo.getEncerramento(),
                processo.getStatus(),
                processo.getCandidato().getId(),
                processo.getVaga().getId()
        );
    }
}
