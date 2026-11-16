package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.specification;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class CandidatoSpecification {

    public static Specification<Candidato> filtrar(
            String nome,
            String cpf,
            String email,
            String formacao,
            String cidade,
            String estado) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nome
            if (nome != null && !nome.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("nome")),
                                "%" + nome.toLowerCase() + "%"
                        )
                );
            }

            // Filtro por CPF
            if (cpf != null && !cpf.isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("cpf"),
                                cpf
                        )
                );
            }

            // Filtro por e-mail
            if (email != null && !email.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                "%" + email.toLowerCase() + "%"
                        )
                );
            }

            // Filtro por formação
            if (formacao != null && !formacao.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("formacao")),
                                "%" + formacao.toLowerCase() + "%"
                        )
                );
            }

            // Filtro por cidade
            if (cidade != null && !cidade.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("endereco").get("cidade")
                                ),
                                "%" + cidade.toLowerCase() + "%"
                        )
                );
            }

            // Filtro por estado
            if (estado != null && !estado.isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("endereco").get("estado"),
                                estado
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
