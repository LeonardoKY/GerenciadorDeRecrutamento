package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CandidatoRepository extends JpaRepository<Candidato, UUID> {
}
