package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VagaRepository extends JpaRepository<Vaga, UUID> {
}
