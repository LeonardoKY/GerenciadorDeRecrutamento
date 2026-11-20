
package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/*
O JpaSpecificationExecutor permite executar consultas dinâmicas utilizando Specifications,
o qual permite combinação de múltiplos filtros e busca com atributos de outras entidades.

Usaremos o Specifications para Candidato para que o usuário consiga fazer buscas com
vários filtros ou usando-os de forma opcional (não precisa aplicar todos os filtros).
*/

public interface CandidatoRepository extends
        JpaRepository<Candidato, UUID>,
        JpaSpecificationExecutor<Candidato> {

    // Busca candidato pelo CPF (busca exata).
    Optional<Candidato> findByCpf(String cpf);

    // Verifica se existe candidato com determinado CPF.
    // Usado no método salvar.
    boolean existsByCpf(String cpf);

    /* Verifica se existe outro candidato com determinado CPF,
    desconsiderando o que possui o ID informado.
    Usamos para atualizar o cadastro do candidato, possibilitando o usuário alterar
    o e-mail mantendo o mesmo CPF.
     */
    boolean existsByCpfAndIdNot(String cpf, UUID id);

    // Verifica se existe candidato com determinado e-mail.
    // Usado no método salvar.
    boolean existsByEmail(String email);

    // Verifica se outro candidato já utiliza o e-mail.
    // Mesma lógica que o existsByCpfAndIdNot, mas para alterar o e-mail mantendo o cpf.
    boolean existsByEmailAndIdNot(String email, UUID id);
}

/*
Os métodos existBy...AndIdNot servem para evitar e-mail e CPF duplicados no banco de dados,
sem impedir o usuário de realizar atualizações válidas.

Obs.: as duas verificações são independentes, então se o usuário quiser alterar tanto o
CPF quanto o e-mail funcionará corretamente.
 */
