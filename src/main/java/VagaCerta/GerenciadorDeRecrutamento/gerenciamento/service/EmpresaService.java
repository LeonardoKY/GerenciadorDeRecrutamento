
package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Empresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.PorteEmpresa;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.repository.EmpresaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    // Verifica se já existe uma empresa com o CNPJ.
    public boolean existePorCnpj(String cnpj) {
        return empresaRepository.existsByCnpj(cnpj);
    }

    // Busca precisa por CNPJ.
    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        return empresaRepository.findByCnpj(cnpj);
    }

    // Busca empresas pelo nome.
    public List<Empresa> buscarPorNome(String nome) {
        return empresaRepository.findByNomeIgnoreCaseContaining(nome);
    }

    // Busca empresas pelo porte.
    public List<Empresa> buscarPorPorte(PorteEmpresa porte) {
        return empresaRepository.findByPorteEmpresa(porte);
    }

    // Busca empresa pelo ID.
    public Optional<Empresa> buscarPorId(UUID id) {
        return empresaRepository.findById(id);
    }

    // Lista todas as empresas.
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    // Salva uma empresa.
    @Transactional
    public Empresa salvar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    // Exclui uma empresa pelo ID.
    @Transactional
    public void excluir(UUID id) {
        empresaRepository.deleteById(id);
    }
}
