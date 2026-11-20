package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.PorteEmpresa;

import java.util.List;
import java.util.UUID;

public record EmpresaDTO(
        UUID id,
        String nome,
        int anoFundacao,
        int qtdeRecrutadores,
        int clientesAtivos,
        String cnpj,
        PorteEmpresa porte,
        List<EnderecoDTO> enderecos // A empresa pode possuir vários endereços.
) {}
