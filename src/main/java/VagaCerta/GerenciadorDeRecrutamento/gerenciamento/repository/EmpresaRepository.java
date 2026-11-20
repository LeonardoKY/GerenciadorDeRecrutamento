package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Empresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.PorteEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    // Para verificar se existe cadastro (verifica por cnpj).
    boolean existsByCnpj(String cnpj);
    // Para validações específicas.
    boolean existsByCnpjAndIdNot(String cnpj, UUID id);

    // Busca precisa por cnpj.
    Optional<Empresa> findByCnpj(String cnpj);

    List<Empresa> findByNomeIgnoreCaseContaining(String nome);
    List<Empresa> findByPorte(PorteEmpresa porte);
}
