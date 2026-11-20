
package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.rest;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.EmpresaDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.EnderecoDTO;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.mapper.EnderecoMapper;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Empresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.PorteEmpresa;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.EmpresaService;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.EnderecoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaRestController {

    private final EmpresaService empresaService;
    private final EnderecoService enderecoService;
    private final EnderecoMapper enderecoMapper;

    public EmpresaRestController(
            EmpresaService empresaService,
            EnderecoService enderecoService,
            EnderecoMapper enderecoMapper) {

        this.empresaService = empresaService;
        this.enderecoService = enderecoService;
        this.enderecoMapper = enderecoMapper;
    }

    // Lista todas as empresas.
    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> listarTodas() {

        List<EmpresaDTO> empresas = empresaService.listarTodas()
                .stream()
                .map(this::converterParaDTO)
                .toList();

        return ResponseEntity.ok(empresas);
    }

    // Busca empresa pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> buscarPorId(
            @PathVariable UUID id) {

        return empresaService.buscarPorId(id)
                .map(this::converterParaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca empresa pelo CNPJ.
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<EmpresaDTO> buscarPorCnpj(
            @PathVariable String cnpj) {

        return empresaService.buscarPorCnpj(cnpj)
                .map(this::converterParaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca empresas pelo nome.
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<EmpresaDTO>> buscarPorNome(
            @PathVariable String nome) {

        List<EmpresaDTO> empresas = empresaService.buscarPorNome(nome)
                .stream()
                .map(this::converterParaDTO)
                .toList();

        return ResponseEntity.ok(empresas);
    }

    // Busca empresas pelo porte.
    @GetMapping("/porte/{porte}")
    public ResponseEntity<List<EmpresaDTO>> buscarPorPorte(
            @PathVariable PorteEmpresa porte) {

        List<EmpresaDTO> empresas = empresaService.buscarPorPorte(porte)
                .stream()
                .map(this::converterParaDTO)
                .toList();

        return ResponseEntity.ok(empresas);
    }

    // Cadastra uma nova empresa.
    @PostMapping
    public ResponseEntity<EmpresaDTO> cadastrar(
            @RequestBody EmpresaDTO dto) {

        Empresa empresa = converterParaEntidade(dto);

        Empresa salva = empresaService.salvar(empresa);

        return ResponseEntity
                .created(URI.create("/api/empresas/" + salva.getId()))
                .body(converterParaDTO(salva));
    }

    // Atualiza os dados cadastrais da empresa.
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody EmpresaDTO dto) {

        Empresa dados = converterParaEntidade(dto);

        Empresa atualizada = empresaService.atualizar(id, dados);

        return ResponseEntity.ok(converterParaDTO(atualizada));
    }

    // Exclui uma empresa.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable UUID id) {

        empresaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    // Lista os endereços de uma empresa.
    @GetMapping("/{id}/enderecos")
    public ResponseEntity<List<EnderecoDTO>> listarEnderecos(
            @PathVariable UUID id) {

        List<EnderecoDTO> enderecos = enderecoService
                .listarPorEmpresa(id)
                .stream()
                .map(enderecoMapper::toDTO)
                .toList();

        return ResponseEntity.ok(enderecos);
    }

    // Adiciona um endereço à empresa.
    @PostMapping("/{id}/enderecos")
    public ResponseEntity<EnderecoDTO> adicionarEndereco(
            @PathVariable UUID id,
            @RequestBody EnderecoDTO dto) {

        Endereco endereco = enderecoMapper.toEntity(dto);

        Endereco salvo = enderecoService.salvarParaEmpresa(id, endereco);

        URI location = URI.create(
                "/api/empresas/" + id + "/enderecos/" + salvo.getId()
        );

        return ResponseEntity
                .created(location)
                .body(enderecoMapper.toDTO(salvo));
    }

    // Atualiza um endereço específico da empresa.
    @PutMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(
            @PathVariable UUID id,
            @PathVariable UUID enderecoId,
            @RequestBody EnderecoDTO dto) {

        verificarPropriedadeEndereco(id, enderecoId);

        Endereco dados = enderecoMapper.toEntity(dto);

        Endereco atualizado = enderecoService.atualizar(
                enderecoId, dados
        );

        return ResponseEntity.ok(enderecoMapper.toDTO(atualizado));
    }

    // Exclui um endereço específico da empresa.
    @DeleteMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<Void> excluirEndereco(
            @PathVariable UUID id,
            @PathVariable UUID enderecoId) {

        verificarPropriedadeEndereco(id, enderecoId);

        enderecoService.excluir(enderecoId);

        return ResponseEntity.noContent().build();
    }

    // Verifica se o endereço pertence à empresa informada.
    private void verificarPropriedadeEndereco(
            UUID empresaId,
            UUID enderecoId) {

        boolean pertence = enderecoService.listarPorEmpresa(empresaId)
                .stream()
                .anyMatch(endereco -> endereco.getId().equals(enderecoId));

        if (!pertence) {
            throw new IllegalArgumentException(
                    "Endereço não encontrado para esta empresa."
            );
        }
    }

    // Converte Empresa para EmpresaDTO.
    private EmpresaDTO converterParaDTO(Empresa empresa) {

        List<EnderecoDTO> enderecos = enderecoService
                .listarPorEmpresa(empresa.getId())
                .stream()
                .map(enderecoMapper::toDTO)
                .toList();

        return new EmpresaDTO(
                empresa.getId(),
                empresa.getNome(),
                empresa.getAnoFundacao(),
                empresa.getQtdeRecrutadores(),
                empresa.getClientesAtivos(),
                empresa.getCnpj(),
                empresa.getPorte(),
                enderecos
        );
    }

    // Converte EmpresaDTO para Empresa.
    private Empresa converterParaEntidade(EmpresaDTO dto) {

        Empresa empresa = new Empresa();

        empresa.setNome(dto.nome());
        empresa.setAnoFundacao(dto.anoFundacao());
        empresa.setQtdeRecrutadores(dto.qtdeRecrutadores());
        empresa.setClientesAtivos(dto.clientesAtivos());
        empresa.setCnpj(dto.cnpj());
        empresa.setPorte(dto.porte());

        return empresa;
    }
}
