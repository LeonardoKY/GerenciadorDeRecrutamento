package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
}
