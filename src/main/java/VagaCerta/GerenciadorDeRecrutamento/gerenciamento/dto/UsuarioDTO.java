package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Perfil;

import java.util.UUID;

public record UsuarioDTO(
        UUID id,
        String nome,
        String email,
        Perfil perfil,
        boolean ativo
) {}

/*
Esse DTO é usado para apresentar os dados dos usuários (diferente do UsuarioCadastroDTO).
 */
