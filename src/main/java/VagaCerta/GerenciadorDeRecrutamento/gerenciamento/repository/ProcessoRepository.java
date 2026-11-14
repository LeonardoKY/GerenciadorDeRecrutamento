package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Processo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessoRepository extends JpaRepository<Processo, UUID> {
}
