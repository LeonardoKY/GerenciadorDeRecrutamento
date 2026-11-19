package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Empresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.CandidatoRepository;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.EmpresaRepository;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.EnderecoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final CandidatoRepository candidatoRepository;
    private final EmpresaRepository empresaRepository;

    public EnderecoService(
            EnderecoRepository enderecoRepository,
            CandidatoRepository candidatoRepository,
            EmpresaRepository empresaRepository) {

        this.enderecoRepository = enderecoRepository;
        this.candidatoRepository = candidatoRepository;
        this.empresaRepository = empresaRepository;
    }

    // Busca um endereço pelo ID.
    public Optional<Endereco> buscarPorId(UUID id) {
        return enderecoRepository.findById(id);
    }

    // Busca o endereço de um candidato.
    public Optional<Endereco> buscarPorCandidato(UUID candidatoId) {
        return enderecoRepository.findByCandidatoId(candidatoId);
    }

    // Lista os endereços de uma empresa.
    public List<Endereco> buscarPorEmpresa(UUID empresaId) {
        return enderecoRepository.findAllByEmpresaId(empresaId);
    }

    // Cadastra o endereço de um candidato.
    @Transactional
    public Endereco cadastrarParaCandidato(
            UUID candidatoId,
            Endereco endereco) {

        Candidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Candidato não encontrado."));

        // Impede que o candidato possua dois endereços.
        if (candidato.getEndereco() != null) {
            throw new IllegalStateException(
                    "O candidato já possui um endereço cadastrado.");
        }

        // Define o proprietário e sincroniza o relacionamento.
        candidato.setEndereco(endereco);

        return enderecoRepository.save(endereco);
    }

    // Adiciona um endereço a uma empresa.
    @Transactional
    public Endereco adicionarParaEmpresa(
            UUID empresaId,
            Endereco endereco) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Empresa não encontrada."));

        // Define o proprietário e sincroniza o relacionamento.
        empresa.adicionarEndereco(endereco);

        return enderecoRepository.save(endereco);
    }

    /* Atualiza os dados de um endereço.

    O proprietário não pode ser alterado, podemos modificar apenas os atributos do endereço.
     */

    @Transactional
    public Endereco atualizar(UUID enderecoId, Endereco dados) {

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Endereço não encontrado."));

        // Atualiza somente os dados do endereço.
        endereco.setCep(dados.getCep());
        endereco.setNumero(dados.getNumero());
        endereco.setComplemento(dados.getComplemento());
        endereco.setLogradouro(dados.getLogradouro());
        endereco.setBairro(dados.getBairro());
        endereco.setCidade(dados.getCidade());
        endereco.setEstado(dados.getEstado());

        // O proprietário original é preservado.
        return enderecoRepository.save(endereco);
    }

    // Exclui um endereço.
    @Transactional
    public void excluir(UUID enderecoId) {

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Endereço não encontrado."));

        // Remove o endereço do candidato.
        if (endereco.getCandidato() != null) {
            endereco.getCandidato().setEndereco(null);
        }

        // Remove o endereço da empresa.
        if (endereco.getEmpresa() != null) {
            endereco.getEmpresa().removerEndereco(endereco);
        }

        /*
         Não precisamos chamar delete() diretamente.
         O orphanRemoval = true das entidades proprietárias,
         realiza a exclusão do endereço no banco de dados.
         */
    }
}
