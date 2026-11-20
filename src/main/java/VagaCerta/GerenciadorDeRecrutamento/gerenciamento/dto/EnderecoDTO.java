package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto;

import java.util.UUID;

public record EnderecoDTO(
        UUID id,
        String cep,
        int numero,
        String complemento,
        String logradouro,
        String bairro,
        String cidade,
        String estado
) {}

// É reutilizado por Candidato e Empresa.
// O proprietário será identificado pelo contexto da operação e pelo ID recebido na rota.
