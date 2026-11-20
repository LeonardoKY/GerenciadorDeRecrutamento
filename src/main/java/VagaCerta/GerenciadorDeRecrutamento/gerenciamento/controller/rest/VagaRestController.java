package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.rest;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.VagaDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.*;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.VagaService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vagas")
public class VagaRestController {

    private final VagaService vagaService;

    public VagaRestController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    // Lista e filtra vagas.
    @GetMapping
    public ResponseEntity<List<VagaDTO>> listar(

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
            StatusVaga status) {

        List<VagaDTO> vagas = vagaService.buscar(
                        aberturaInicial,
                        aberturaFinal,
                        fechamentoInicial,
                        fechamentoFinal,
                        modalidade,
                        senioridade,
                        area,
                        status
                ).stream()
                .map(this::converterParaDTO)
                .toList();

        return ResponseEntity.ok(vagas);
    }

    // Busca uma vaga pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<VagaDTO> buscarPorId(
            @PathVariable UUID id) {

        return vagaService.buscarPorId(id)
                .map(this::converterParaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastra uma nova vaga.
    @PostMapping
    public ResponseEntity<VagaDTO> cadastrar(
            @RequestBody VagaDTO dto) {

        Vaga vaga = converterParaEntidade(dto);

        Vaga salva = vagaService.salvar(vaga);

        URI location = URI.create(
                "/api/vagas/" + salva.getId()
        );

        return ResponseEntity
                .created(location)
                .body(converterParaDTO(salva));
    }

    // Atualiza uma vaga existente.
    @PutMapping("/{id}")
    public ResponseEntity<VagaDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody VagaDTO dto) {

        Vaga dados = converterParaEntidade(dto);

        Vaga atualizada = vagaService.atualizar(id, dados);

        return ResponseEntity.ok(
                converterParaDTO(atualizada)
        );
    }

    // Exclui uma vaga.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable UUID id) {

        vagaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    // Converte Vaga para VagaDTO.
    private VagaDTO converterParaDTO(Vaga vaga) {

        return new VagaDTO(
                vaga.getId(),
                vaga.getAbertura(),
                vaga.getFechamento(),
                vaga.getSalarioMensal(),
                vaga.getModalidade(),
                vaga.getSenioridade(),
                vaga.getArea(),
                vaga.getStatus(),
                vaga.getFeedback(),
                vaga.getEmpresa().getId()
        );
    }

    // Converte VagaDTO para Vaga.
    private Vaga converterParaEntidade(VagaDTO dto) {

        Vaga vaga = new Vaga();

        vaga.setAbertura(dto.abertura());
        vaga.setFechamento(dto.fechamento());
        vaga.setSalarioMensal(dto.salarioMensal());
        vaga.setModalidade(dto.modalidade());
        vaga.setSenioridade(dto.senioridade());
        vaga.setArea(dto.area());
        vaga.setStatus(dto.status());
        vaga.setFeedback(dto.feedback());

        // Associa a empresa informada.
        if (dto.empresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.empresaId());

            vaga.setEmpresa(empresa);
        }

        return vaga;
    }
}