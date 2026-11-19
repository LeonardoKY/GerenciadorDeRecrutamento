package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    // Busca por CEP
    List<Endereco> findByCep(String cep);

    // Busca o endereço de um candidato.
    Optional<Endereco> findByCandidatoId(UUID candidatoId);
    // Busca precisa por CPF (do candidato)
    Optional<Endereco> findByCandidatoCpf(String cpf);

    // Lista os endereços de uma empresa.
    List<Endereco> findAllByEmpresaId(UUID empresaId);
    // Busca por CNPJ da empresa
    List<Endereco> findAllByEmpresaCnpj(String cnpj);

}
