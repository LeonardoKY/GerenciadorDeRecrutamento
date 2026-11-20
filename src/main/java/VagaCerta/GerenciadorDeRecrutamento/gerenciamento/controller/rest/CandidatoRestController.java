
package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.rest;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.CandidatoDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.EnderecoDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.mapper.EnderecoMapper;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.CandidatoService;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.EnderecoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidatos")
public class CandidatoRestController {

    private final CandidatoService candidatoService;
    private final EnderecoService enderecoService;
    private final EnderecoMapper enderecoMapper;

    public CandidatoRestController(
            CandidatoService candidatoService,
            EnderecoService enderecoService,
            EnderecoMapper enderecoMapper) {
        this.candidatoService = candidatoService;
        this.enderecoService = enderecoService;
        this.enderecoMapper = enderecoMapper;
    }

    @GetMapping
    public ResponseEntity<List<CandidatoDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String formacao,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado) {

        List<CandidatoDTO> resultado = candidatoService.buscar(
                nome, cpf, email, formacao, cidade, estado
        ).stream().map(this::paraDTO).toList();

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidatoDTO> buscarPorId(
            @PathVariable UUID id) {

        return candidatoService.buscarPorId(id)
                .map(candidato -> ResponseEntity.ok(paraDTO(candidato)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<CandidatoDTO> buscarPorCpf(
            @PathVariable String cpf) {

        return candidatoService.buscarPorCpf(cpf)
                .map(candidato -> ResponseEntity.ok(paraDTO(candidato)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CandidatoDTO> cadastrar(
            @RequestBody CandidatoDTO dto) {

        Candidato candidato = candidatoService.salvar(deDTO(dto));

        return ResponseEntity
                .created(URI.create("/api/candidatos/" + candidato.getId()))
                .body(paraDTO(candidato));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidatoDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody CandidatoDTO dto) {

        Candidato atualizado = candidatoService.atualizar(id, deDTO(dto));

        return ResponseEntity.ok(paraDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        candidatoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // Endereço do candidato

    @GetMapping("/{id}/endereco")
    public ResponseEntity<EnderecoDTO> buscarEndereco(
            @PathVariable UUID id) {

        return enderecoService.buscarPorCandidato(id)
                .map(endereco -> ResponseEntity.ok(enderecoMapper.toDTO(endereco)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/endereco")
    public ResponseEntity<EnderecoDTO> cadastrarEndereco(
            @PathVariable UUID id,
            @RequestBody EnderecoDTO dto) {

        Endereco endereco = enderecoService.salvarParaCandidato(
                id, enderecoMapper.toEntity(dto)
        );

        return ResponseEntity.ok(enderecoMapper.toDTO(endereco));
    }

    @PutMapping("/{id}/endereco")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(
            @PathVariable UUID id,
            @RequestBody EnderecoDTO dto) {

        Endereco enderecoAtual = enderecoService.buscarPorCandidato(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Endereço não encontrado."));

        Endereco atualizado = enderecoService.atualizar(
                enderecoAtual.getId(),
                enderecoMapper.toEntity(dto)
        );

        return ResponseEntity.ok(enderecoMapper.toDTO(atualizado));
    }

    @DeleteMapping("/{id}/endereco")
    public ResponseEntity<Void> excluirEndereco(
            @PathVariable UUID id) {

        Endereco endereco = enderecoService.buscarPorCandidato(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Endereço não encontrado."));

        enderecoService.excluir(endereco.getId());

        return ResponseEntity.noContent().build();
    }

    // Conversão de Candidato

    private CandidatoDTO paraDTO(Candidato candidato) {
        EnderecoDTO endereco = candidato.getEndereco() == null
                ? null
                : enderecoMapper.toDTO(candidato.getEndereco());

        return new CandidatoDTO(
                candidato.getId(),
                candidato.getNome(),
                candidato.getCpf(),
                candidato.getEmail(),
                candidato.getNascimento(),
                candidato.getAnosExperiencia(),
                candidato.getGenero(),
                candidato.getFormacao(),
                endereco
        );
    }

    private Candidato deDTO(CandidatoDTO dto) {
        Candidato candidato = new Candidato();
        candidato.setNome(dto.nome());
        candidato.setCpf(dto.cpf());
        candidato.setEmail(dto.email());
        candidato.setNascimento(dto.nascimento());
        candidato.setAnosExperiencia(dto.anosExperiencia());
        candidato.setGenero(dto.genero());
        candidato.setFormacao(dto.formacao());
        return candidato;
    }
}
