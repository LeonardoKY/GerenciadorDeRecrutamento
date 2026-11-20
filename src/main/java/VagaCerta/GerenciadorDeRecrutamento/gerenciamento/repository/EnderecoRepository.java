package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    List<Endereco> findByEmpresaId(UUID empresaId);

    Optional<Endereco> findByCandidatoId(UUID candidatoId);

    boolean existsByCandidatoId(UUID candidatoId);
}
