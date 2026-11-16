package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.*;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.specification.VagaSpecification;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.VagaRepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

public class VagaService {

    private final VagaRepository vagaRepository;

    public VagaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    /*
    A busca por data terá a entrada apenas da data sem o horário.
    Faremos a busca por intervalos para data de abertura e fechamento da vaga
     */

    public List<Vaga> buscar(
            LocalDate aberturaInicial,
            LocalDate aberturaFinal,
            LocalDate fechamentoInicial,
            LocalDate fechamentoFinal,
            Modalidade modalidade,
            Senioridade senioridade,
            AreaAtuacao area,
            StatusVaga status) {

        Specification<Vaga> specification =
                VagaSpecification.filtrar(
                        aberturaInicial,
                        aberturaFinal,
                        fechamentoInicial,
                        fechamentoFinal,
                        modalidade,
                        senioridade,
                        area,
                        status
                );

        return vagaRepository.findAll(specification);
    }
}
