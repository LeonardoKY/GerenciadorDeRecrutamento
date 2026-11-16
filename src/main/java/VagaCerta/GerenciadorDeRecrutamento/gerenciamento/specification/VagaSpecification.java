package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.specification;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Vaga;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Modalidade;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Senioridade;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.AreaAtuacao;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.StatusVaga;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VagaSpecification {

    public static Specification<Vaga> filtrar(
            LocalDate aberturaInicial,
            LocalDate aberturaFinal,
            LocalDate fechamentoInicial,
            LocalDate fechamentoFinal,
            Modalidade modalidade,
            Senioridade senioridade,
            AreaAtuacao area,
            StatusVaga status) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Filtro por período de abertura
            if (aberturaInicial != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.<LocalDateTime>get("abertura"),
                                aberturaInicial.atStartOfDay()
                        )
                );
            }

            if (aberturaFinal != null) {
                predicates.add(
                        criteriaBuilder.lessThan(
                                root.<LocalDateTime>get("abertura"),
                                aberturaFinal.plusDays(1).atStartOfDay()
                        )
                );
            }

            // Filtro por período de fechamento
            // Fechamento NULL será considerado como a data atual.
            if (fechamentoInicial != null || fechamentoFinal != null) {

                LocalDateTime dataAtual =
                        LocalDate.now().atStartOfDay();

                var fechamentoEfetivo =
                        criteriaBuilder.coalesce(
                                root.<LocalDateTime>get("fechamento"),
                                criteriaBuilder.literal(dataAtual)
                        );

                if (fechamentoInicial != null) {
                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(
                                    fechamentoEfetivo,
                                    fechamentoInicial.atStartOfDay()
                            )
                    );
                }

                if (fechamentoFinal != null) {
                    predicates.add(
                            criteriaBuilder.lessThan(
                                    fechamentoEfetivo,
                                    fechamentoFinal.plusDays(1).atStartOfDay()
                            )
                    );
                }
            }

            // Filtro por modalidade
            if (modalidade != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("modalidade"),
                                modalidade
                        )
                );
            }

            // Filtro por senioridade
            if (senioridade != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("senioridade"),
                                senioridade
                        )
                );
            }

            // Filtro por área de atuação
            if (area != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("area"),
                                area
                        )
                );
            }

            // Filtro por status
            if (status != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
