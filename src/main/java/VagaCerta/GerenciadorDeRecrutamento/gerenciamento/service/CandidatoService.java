
package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.specification.CandidatoSpecification;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.CandidatoRepository;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.ProcessoRepository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final ProcessoRepository processoRepository;

    public CandidatoService(
            CandidatoRepository candidatoRepository,
            ProcessoRepository processoRepository) {

        this.candidatoRepository = candidatoRepository;
        this.processoRepository = processoRepository;
    }

    // Busca candidatos utilizando filtros opcionais.
    public List<Candidato> buscar(
            String nome,
            String cpf,
            String email,
            String formacao,
            String cidade,
            String estado) {

        Specification<Candidato> specification =
                CandidatoSpecification.filtrar(
                        nome, cpf, email, formacao, cidade, estado
                );

        return candidatoRepository.findAll(specification);
    }

    // Busca candidato pelo ID.
    public Optional<Candidato> buscarPorId(UUID id) {
        return candidatoRepository.findById(id);
    }

    // Busca candidato pelo CPF.
    public Optional<Candidato> buscarPorCpf(String cpf) {
        return candidatoRepository.findByCpf(cpf);
    }

    // Verifica se existe candidato cadastrado com determinado CPF.
    public boolean existePorCpf(String cpf) {
        return candidatoRepository.existsByCpf(cpf);
    }

    // Lista todos os candidatos.
    public List<Candidato> listarTodos() {
        return candidatoRepository.findAll();
    }

    // Cadastra um novo candidato.
    @Transactional
    public Candidato salvar(Candidato candidato) {

        if (candidatoRepository.existsByCpf(candidato.getCpf())) {
            throw new IllegalStateException(
                    "Já existe um candidato cadastrado com este CPF.");
        }

        if (candidatoRepository.existsByEmail(candidato.getEmail())) {
            throw new IllegalStateException(
                    "Já existe um candidato cadastrado com este e-mail.");
        }

        return candidatoRepository.save(candidato);
    }

    // Atualiza os dados de um candidato existente.
    @Transactional
    public Candidato atualizar(UUID id, Candidato dados) {

        Candidato candidato = candidatoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Candidato não encontrado."));

        // Verifica se o CPF pertence a outro candidato.
        if (candidatoRepository.existsByCpfAndIdNot(
                dados.getCpf(), id)) {

            throw new IllegalStateException(
                    "Já existe outro candidato cadastrado com este CPF.");
        }

        // Verifica se o e-mail pertence a outro candidato.
        if (candidatoRepository.existsByEmailAndIdNot(
                dados.getEmail(), id)) {

            throw new IllegalStateException(
                    "Já existe outro candidato cadastrado com este e-mail.");
        }

        // Atualiza os dados cadastrais.
        candidato.setNome(dados.getNome());
        candidato.setCpf(dados.getCpf());
        candidato.setEmail(dados.getEmail());
        candidato.setNascimento(dados.getNascimento());
        candidato.setAnosExperiencia(dados.getAnosExperiencia());
        candidato.setGenero(dados.getGenero());
        candidato.setFormacao(dados.getFormacao());

        // O endereço é preservado e gerenciado pelo EnderecoService.

        return candidatoRepository.save(candidato);
    }

    // Exclui um candidato pelo ID.
    @Transactional
    public void excluir(UUID id) {

        Candidato candidato = candidatoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Candidato não encontrado."));

        // Impede a exclusão de candidatos com histórico de processos.
        if (processoRepository.existsByCandidatoId(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir um candidato que possui processos seletivos registrados.");
        }

        candidatoRepository.delete(candidato);
    }
}
