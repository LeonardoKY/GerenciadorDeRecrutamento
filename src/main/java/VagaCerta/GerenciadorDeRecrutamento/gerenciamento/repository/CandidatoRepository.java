package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import java.util.Optional;

public interface CandidatoRepository extends JpaRepository<Candidato, UUID>, JpaSpecificationExecutor<Candidato> {
    // Para verificar se existe cadastro por cpf.
    boolean existsByCpf(String cpf);
}
/*
O JpaSpecificationExecutor permite executar consultas dinâmicas utilizando Specifications,
o qual permite combinação de múltiplos filtros e busca com atributos de outras entidades.

Usaremos o Specifications para Candidato para que o usuário consiga fazer buscas com
vários filtros ou usando-os de forma opcional (não precisa aplicar todos os filtros).
 */
