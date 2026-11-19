package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.*;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /* Transições permitidas entre os estados:
    Como o status do processo deve progredir de forma linear,
    usaremos o Map para definir os estados liberados em cada etapa.
    Ex: TRIAGEM libera os estados AVALIACAO, REPROVADO e DESISTIU

    Obs.: CONTRATADO, REPROVADO e DESISTIU são estados finais.
     */
    private static final Map<StatusProcesso, Set<StatusProcesso>>
            TRANSICOES = Map.of(

            StatusProcesso.INSCRITO,
            Set.of(StatusProcesso.TRIAGEM,
                    StatusProcesso.DESISTIU),

            StatusProcesso.TRIAGEM,
            Set.of(StatusProcesso.AVALIACAO,
                    StatusProcesso.REPROVADO,
                    StatusProcesso.DESISTIU),

            StatusProcesso.AVALIACAO,
            Set.of(StatusProcesso.ENTREVISTA,
                    StatusProcesso.REPROVADO,
                    StatusProcesso.DESISTIU),

            StatusProcesso.ENTREVISTA,
            Set.of(StatusProcesso.APROVADO,
                    StatusProcesso.REPROVADO,
                    StatusProcesso.DESISTIU),

            StatusProcesso.APROVADO,
            Set.of(StatusProcesso.CONTRATADO,
                    StatusProcesso.DESISTIU)
    );

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

        // Define os valores iniciais.
        processo.setInscricao(LocalDateTime.now());
        processo.setStatus(StatusProcesso.INSCRITO);

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

        StatusProcesso statusAtual = processo.getStatus();

        // Verifica se a transição é permitida.
        Set<StatusProcesso> permitidos =
                TRANSICOES.getOrDefault(statusAtual, Set.of());

        if (!permitidos.contains(novoStatus)) {
            throw new IllegalStateException(
                    "Transição de " + statusAtual +
                            " para " + novoStatus + " não permitida.");
        }

        // Atualiza o estado.
        processo.setStatus(novoStatus);

        // Registra a data de encerramento.
        if (novoStatus == StatusProcesso.CONTRATADO ||
                novoStatus == StatusProcesso.REPROVADO ||
                novoStatus == StatusProcesso.DESISTIU) {

            processo.setEncerramento(LocalDateTime.now());
        }

        return processoRepository.save(processo);
    }

    // Busca um processo pelo identificador.
    public java.util.Optional<Processo> buscarPorId(UUID id) {
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
