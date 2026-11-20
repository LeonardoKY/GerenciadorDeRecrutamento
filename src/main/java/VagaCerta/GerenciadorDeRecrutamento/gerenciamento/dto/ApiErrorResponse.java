package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}

/*
Essa classe serve para padronizar as respostas de erro da API.
Ex. do formato padronizado:
{
  "timestamp": "2026-11-20T19:00:00-03:00",
  "status": 409,
  "error": "Conflict",
  "message": "O candidato já está inscrito nesta vaga.",
  "path": "/api/processos"
}
 */
