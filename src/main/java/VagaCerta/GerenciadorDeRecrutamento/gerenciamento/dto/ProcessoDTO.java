package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.StatusProcesso;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProcessoDTO(
        UUID id,
        double pretensaoSalarial,
        LocalDateTime inscricao,
        LocalDateTime encerramento,
        StatusProcesso status,
        UUID candidatoId,
        UUID vagaId
) {}

/*
Obs.: aqui não há campo para alterar o status do processo,
a atualização do status é feita pelo ProcessoService usando o
método atualizarStatus.
 */
