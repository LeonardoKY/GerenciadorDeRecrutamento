package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.*;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProcessoService {

    private final ProcessoRepository processoRepository;
    private final CandidatoRepository candidatoRepository;
    private final VagaRepository vagaRepository;

    public ProcessoService(
            ProcessoRepository processoRepository,
            CandidatoRepository candidatoRepository,
            VagaRepository vagaRepository) {

        this.processoRepository = processoRepository;
        this.candidatoRepository = candidatoRepository;
        this.vagaRepository = vagaRepository;
    }

    // Realiza a inscrição de um candidato em uma vaga.
    @Transactional
    public Processo inscrever(
            UUID candidatoId,
            UUID vagaId,
            double pretensaoSalarial) {

        // Verifica se o candidato existe.
        Candidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Candidato não encontrado."));

        // Verifica se a vaga existe.
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vaga não encontrada."));

        // Verifica se a vaga está aberta.
        if (vaga.getStatus() != StatusVaga.ABERTA) {
            throw new IllegalStateException(
                    "Não é possível se inscrever em uma vaga fechada ou cancelada.");
        }

        // Impede inscrições duplicadas.
        if (processoRepository.existsByCandidatoIdAndVagaId(
                candidatoId, vagaId)) {

            throw new IllegalStateException(
                    "O candidato já está inscrito nesta vaga.");
        }

        // Cria o processo.
        Processo processo = new Processo();

        processo.setCandidato(candidato);
        processo.setVaga(vaga);
        processo.setPretensaoSalarial(pretensaoSalarial);

        // Define a data de inscrição.
        processo.setInscricao(LocalDateTime.now());

        // O status inicial INSCRITO é definido pela entidade (Processo).

        return processoRepository.save(processo);
    }

    // Atualiza o estado de um processo.
    @Transactional
    public Processo atualizarStatus(
            UUID processoId,
            StatusProcesso novoStatus) {

        // Recupera o processo.
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Processo não encontrado."));

        // Solicita à entidade a validação e atualização do status.
        processo.atualizarStatus(novoStatus);

        return processoRepository.save(processo);
    }

    // Busca um processo pelo identificador.
    public Optional<Processo> buscarPorId(UUID id) {
        return processoRepository.findById(id);
    }

    // Lista os processos de um candidato.
    public List<Processo> buscarPorCandidato(UUID candidatoId) {
        return processoRepository.findByCandidatoId(candidatoId);
    }

    // Lista os processos de uma vaga.
    public List<Processo> buscarPorVaga(UUID vagaId) {
        return processoRepository.findByVagaId(vagaId);
    }

    // Lista todos os processos.
    public List<Processo> listarTodos() {
        return processoRepository.findAll();
    }
}
