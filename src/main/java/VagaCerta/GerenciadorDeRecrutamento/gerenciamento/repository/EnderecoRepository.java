package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    // Busca precisa por CPF (do candidato)
    Optional<Endereco> findByCandidatoCpf(String cpf);
    // Busca precisa por CNPJ (da empresa)
    Optional<Endereco> findByEmpresaCnpj(String cnpj);

    // Busca por CEP
    List<Endereco> findByCep(String cep);
    // Busca pelo nome da empresa
    List<Endereco> findByCandidatoNomeContainingIgnoreCase(String nome);
}
