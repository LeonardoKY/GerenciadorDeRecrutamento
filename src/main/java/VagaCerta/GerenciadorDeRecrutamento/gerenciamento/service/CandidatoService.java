package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Candidato;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.specification.CandidatoSpecification;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.CandidatoRepository;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CandidatoService {
    private final CandidatoRepository candidatoRepository;

    public CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    public List<Candidato> buscar(
            String nome,
            String cpf,
            String email,
            String formacao,
            String cidade,
            String estado) {

        Specification<Candidato> specification =
                CandidatoSpecification.filtrar(
                        nome, cpf, email, formacao, cidade, estado
                );

        return candidatoRepository.findAll(specification);
    }
}
