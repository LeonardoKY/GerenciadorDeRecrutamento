package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Vaga;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface VagaRepository extends JpaRepository<Vaga, UUID>,
        JpaSpecificationExecutor<Vaga> {
    // Verifica se existem vagas vinculadas à empresa.
    boolean existsByEmpresaId(UUID empresaId);
}
