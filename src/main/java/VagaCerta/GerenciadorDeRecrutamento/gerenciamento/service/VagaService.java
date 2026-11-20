
package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.*;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.specification.VagaSpecification;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.VagaRepository;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.EmpresaRepository;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.ProcessoRepository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class VagaService {

    private final VagaRepository vagaRepository;
    private final EmpresaRepository empresaRepository;
    private final ProcessoRepository processoRepository;

    public VagaService(
            VagaRepository vagaRepository,
            EmpresaRepository empresaRepository,
            ProcessoRepository processoRepository) {

        this.vagaRepository = vagaRepository;
        this.empresaRepository = empresaRepository;
        this.processoRepository = processoRepository;
    }

    /*
    A busca por data terá a entrada apenas da data sem o horário.
    Faremos a busca por intervalos para data de abertura e fechamento da vaga.
    Os intervalos são definidos por Inicial (aberturaInicial e fechamentoInicial)
    e Final (aberturaFinal e fechamentoFinal).
     */

    // Busca vagas utilizando filtros opcionais.
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

    // Busca vaga pelo ID.
    public Optional<Vaga> buscarPorId(UUID id) {
        return vagaRepository.findById(id);
    }

    // Lista todas as vagas.
    public List<Vaga> listarTodas() {
        return vagaRepository.findAll();
    }

    // Cadastra uma nova vaga.
    @Transactional
    public Vaga salvar(Vaga vaga) {

        validarDados(vaga);

        if (vaga.getEmpresa() == null ||
                vaga.getEmpresa().getId() == null) {

            throw new IllegalArgumentException(
                    "É necessário informar uma empresa para cadastrar a vaga.");
        }

        // Verifica se a empresa existe.
        Empresa empresa = empresaRepository.findById(
                vaga.getEmpresa().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Empresa não encontrada."));

        // Define a empresa existente como proprietária da vaga.
        vaga.setEmpresa(empresa);

        return vagaRepository.save(vaga);
    }

    // Atualiza os dados de uma vaga existente.
    @Transactional
    public Vaga atualizar(UUID id, Vaga dados) {

        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vaga não encontrada."));

        validarDados(dados);

        // Atualiza os dados da vaga.
        vaga.setAbertura(dados.getAbertura());
        vaga.setFechamento(dados.getFechamento());
        vaga.setSalarioMensal(dados.getSalarioMensal());
        vaga.setModalidade(dados.getModalidade());
        vaga.setSenioridade(dados.getSenioridade());
        vaga.setArea(dados.getArea());
        vaga.setStatus(dados.getStatus());

        // A empresa original é preservada.
        // O feedback também não é alterado nesta operação.

        return vagaRepository.save(vaga);
    }

    // Exclui uma vaga pelo ID.
    @Transactional
    public void excluir(UUID id) {

        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vaga não encontrada."));

        // Preserva o histórico dos processos seletivos.
        if (processoRepository.existsByVagaId(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir uma vaga que possui processos seletivos registrados.");
        }

        vagaRepository.delete(vaga);
    }

    // Valida os dados obrigatórios da vaga.
    private void validarDados(Vaga vaga) {

        if (vaga.getAbertura() == null) {
            throw new IllegalArgumentException(
                    "A data de abertura é obrigatória.");
        }

        if (vaga.getSalarioMensal() <= 0 ||
                !Double.isFinite(vaga.getSalarioMensal())) {

            throw new IllegalArgumentException(
                    "O salário mensal deve ser maior que zero.");
        }

        if (vaga.getModalidade() == null ||
                vaga.getSenioridade() == null ||
                vaga.getArea() == null ||
                vaga.getStatus() == null) {

            throw new IllegalArgumentException(
                    "Modalidade, senioridade, área e status são obrigatórios.");
        }

        if (vaga.getFechamento() != null &&
                vaga.getFechamento().isBefore(vaga.getAbertura())) {

            throw new IllegalArgumentException(
                    "A data de fechamento não pode ser anterior à data de abertura.");
        }

        if (!Float.isFinite(vaga.getFeedback()) ||
                vaga.getFeedback() < 0 ||
                vaga.getFeedback() > 10) {

            throw new IllegalArgumentException(
                    "O feedback deve estar entre 0 e 10.");
        }
    }
}
