package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.AreaAtuacao;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Modalidade;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Senioridade;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.StatusVaga;

import java.time.LocalDateTime;
import java.util.UUID;

public record VagaDTO(
        UUID id,
        LocalDateTime abertura,
        LocalDateTime fechamento,
        double salarioMensal,
        Modalidade modalidade,
        Senioridade senioridade,
        AreaAtuacao area,
        StatusVaga status,
        float feedback,
        UUID empresaId// Usado pelo controller para identificar a empresa no cadastro da vaga.
) {}
