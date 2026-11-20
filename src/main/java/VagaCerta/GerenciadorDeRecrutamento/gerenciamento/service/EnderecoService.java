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
    private final EmpresaRepository empresaRepository;
    private final CandidatoRepository candidatoRepository;

    public EnderecoService(
            EnderecoRepository enderecoRepository,
            EmpresaRepository empresaRepository,
            CandidatoRepository candidatoRepository) {

        this.enderecoRepository = enderecoRepository;
        this.empresaRepository = empresaRepository;
        this.candidatoRepository = candidatoRepository;
    }

    // Busca um endereço pelo ID.
    public Optional<Endereco> buscarPorId(UUID id) {
        return enderecoRepository.findById(id);
    }

    // Lista os endereços de uma empresa.
    public List<Endereco> listarPorEmpresa(UUID empresaId) {

        if (!empresaRepository.existsById(empresaId)) {
            throw new IllegalArgumentException(
                    "Empresa não encontrada.");
        }

        return enderecoRepository.findByEmpresaId(empresaId);
    }

    // Busca o endereço de um candidato.
    public Optional<Endereco> buscarPorCandidato(UUID candidatoId) {

        if (!candidatoRepository.existsById(candidatoId)) {
            throw new IllegalArgumentException(
                    "Candidato não encontrado.");
        }

        return enderecoRepository.findByCandidatoId(candidatoId);
    }

    // Cadastra um endereço para uma empresa.
    @Transactional
    public Endereco salvarParaEmpresa(UUID empresaId, Endereco endereco) {

        validarNovoEndereco(endereco);

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Empresa não encontrada."));

        endereco.setEmpresa(empresa);
        endereco.setCandidato(null);

        endereco.setCep(normalizarCep(endereco.getCep()));

        return enderecoRepository.save(endereco);
    }

    // Cadastra o endereço de um candidato.
    @Transactional
    public Endereco salvarParaCandidato(UUID candidatoId, Endereco endereco) {

        validarNovoEndereco(endereco);

        Candidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Candidato não encontrado."));

        if (enderecoRepository.existsByCandidatoId(candidatoId)) {
            throw new IllegalStateException(
                    "O candidato já possui um endereço cadastrado.");
        }

        endereco.setCandidato(candidato);
        endereco.setEmpresa(null);

        endereco.setCep(normalizarCep(endereco.getCep()));

        return enderecoRepository.save(endereco);
    }

    // Atualiza os dados de um endereço, preservando seu proprietário.
    @Transactional
    public Endereco atualizar(UUID id, Endereco dados) {

        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Endereço não encontrado."));

        validarDados(dados);

        endereco.setCep(normalizarCep(dados.getCep()));
        endereco.setNumero(dados.getNumero());
        endereco.setComplemento(dados.getComplemento().trim());
        endereco.setLogradouro(dados.getLogradouro().trim());
        endereco.setBairro(dados.getBairro().trim());
        endereco.setCidade(dados.getCidade().trim());
        endereco.setEstado(dados.getEstado().trim());

        return enderecoRepository.save(endereco);
    }

    // Exclui um endereço pelo ID.
    @Transactional
    public void excluir(UUID id) {

        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Endereço não encontrado."));

        enderecoRepository.delete(endereco);
    }

    // Valida um novo endereço.
    private void validarNovoEndereco(Endereco endereco) {

        if (endereco == null) {
            throw new IllegalArgumentException(
                    "Os dados do endereço não foram informados.");
        }

        if (endereco.getId() != null) {
            throw new IllegalArgumentException(
                    "Um novo endereço não deve possuir ID informado.");
        }

        validarDados(endereco);
    }

    // Valida os campos do endereço.
    private void validarDados(Endereco endereco) {

        if (endereco == null) {
            throw new IllegalArgumentException(
                    "Os dados do endereço não foram informados.");
        }

        String cep = normalizarCep(endereco.getCep());

        if (!cep.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "O CEP deve possuir 8 dígitos.");
        }

        if (endereco.getNumero() < 0) {
            throw new IllegalArgumentException(
                    "O número do endereço não pode ser negativo.");
        }

        validarTexto(endereco.getComplemento(), "Complemento", 50, true);
        validarTexto(endereco.getLogradouro(), "Logradouro", 50, false);
        validarTexto(endereco.getBairro(), "Bairro", 50, false);
        validarTexto(endereco.getCidade(), "Cidade", 50, false);
        validarTexto(endereco.getEstado(), "Estado", 50, false);
    }

    // Valida campos de texto e seus limites.
    private void validarTexto(
            String valor,
            String campo,
            int tamanhoMaximo,
            boolean permitirVazio) {

        if (valor == null || (!permitirVazio && valor.isBlank())) {
            throw new IllegalArgumentException(
                    campo + " é obrigatório.");
        }

        if (valor.length() > tamanhoMaximo) {
            throw new IllegalArgumentException(
                    campo + " deve possuir no máximo " +
                            tamanhoMaximo + " caracteres.");
        }
    }

    // Remove caracteres não numéricos do CEP.
    private String normalizarCep(String cep) {

        if (cep == null) {
            return "";
        }

        return cep.replaceAll("\\D", "");
    }
}
