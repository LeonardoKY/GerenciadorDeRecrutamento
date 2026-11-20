package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Empresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.PorteEmpresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.EmpresaRepository;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.VagaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final VagaRepository vagaRepository;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            VagaRepository vagaRepository) {

        this.empresaRepository = empresaRepository;
        this.vagaRepository = vagaRepository;
    }

    // Verifica se já existe uma empresa com o CNPJ.
    public boolean existePorCnpj(String cnpj) {
        return empresaRepository.existsByCnpj(normalizarCnpj(cnpj));
    }

    // Busca precisa por CNPJ.
    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        return empresaRepository.findByCnpj(normalizarCnpj(cnpj));
    }

    // Busca empresas pelo nome.
    public List<Empresa> buscarPorNome(String nome) {
        return empresaRepository.findByNomeIgnoreCaseContaining(nome);
    }

    // Busca empresas pelo porte.
    public List<Empresa> buscarPorPorte(PorteEmpresa porte) {
        return empresaRepository.findByPorte(porte);
    }

    // Busca empresa pelo ID.
    public Optional<Empresa> buscarPorId(UUID id) {
        return empresaRepository.findById(id);
    }

    // Lista todas as empresas.
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    // Cadastra uma nova empresa.
    @Transactional
    public Empresa salvar(Empresa empresa) {

        if (empresa.getId() != null) {
            throw new IllegalArgumentException(
                    "Uma nova empresa não deve possuir ID informado.");
        }

        validarDados(empresa);

        String cnpj = normalizarCnpj(empresa.getCnpj());

        if (empresaRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException(
                    "Já existe uma empresa cadastrada com este CNPJ.");
        }

        empresa.setCnpj(cnpj);

        return empresaRepository.save(empresa);
    }

    // Atualiza os dados de uma empresa.
    @Transactional
    public Empresa atualizar(UUID id, Empresa dados) {

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Empresa não encontrada."));

        validarDados(dados);

        // O CNPJ e os endereços existentes são preservados.
        empresa.setNome(dados.getNome().trim());
        empresa.setAnoFundacao(dados.getAnoFundacao());
        empresa.setQtdeRecrutadores(dados.getQtdeRecrutadores());
        empresa.setClientesAtivos(dados.getClientesAtivos());
        empresa.setPorte(dados.getPorte());

        return empresaRepository.save(empresa);
    }

    // Exclui uma empresa pelo ID.
    @Transactional
    public void excluir(UUID id) {

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Empresa não encontrada."));

        // Impede a exclusão se houver vagas associadas.
        if (vagaRepository.existsByEmpresaId(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir a empresa, pois existem vagas associadas a ela.");
        }

        /*
         Os endereços associados serão excluídos em cascata,
         conforme CascadeType.ALL e orphanRemoval da entidade Empresa.
         */
        empresaRepository.delete(empresa);
    }

    // Valida os dados obrigatórios da empresa.
    private void validarDados(Empresa empresa) {

        if (empresa == null) {
            throw new IllegalArgumentException(
                    "Os dados da empresa não foram informados.");
        }

        if (empresa.getNome() == null || empresa.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da empresa é obrigatório.");
        }

        if (empresa.getAnoFundacao() <= 0 ||
                empresa.getAnoFundacao() > Year.now().getValue()) {
            throw new IllegalArgumentException(
                    "O ano de fundação informado é inválido.");
        }

        if (empresa.getQtdeRecrutadores() < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de recrutadores não pode ser negativa.");
        }

        if (empresa.getClientesAtivos() < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de clientes ativos não pode ser negativa.");
        }

        if (empresa.getPorte() == null) {
            throw new IllegalArgumentException(
                    "O porte da empresa é obrigatório.");
        }

        String cnpj = normalizarCnpj(empresa.getCnpj());

        if (cnpj.length() != 14 || !cnpj.matches("\\d{14}")) {
            throw new IllegalArgumentException(
                    "O CNPJ deve possuir 14 dígitos.");
        }
    }

    // Remove pontos, barras, traços e outros caracteres do CNPJ.
    private String normalizarCnpj(String cnpj) {

        if (cnpj == null) {
            return "";
        }

        return cnpj.replaceAll("\\D", "");
    }
}
