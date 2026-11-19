package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Processo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProcessoRepository extends JpaRepository<Processo, UUID> {

    // Verifica se o candidato já está inscrito na vaga.
    boolean existsByCandidatoIdAndVagaId(UUID candidatoId, UUID vagaId);
    // Busca todos os processos de um candidato pelo ID do candidato.
    List<Processo> findByCandidatoId(UUID candidatoId);
    // Busca todos os processos de uma vaga pelo ID da vaga.
    List<Processo> findByVagaId(UUID vagaId);
}