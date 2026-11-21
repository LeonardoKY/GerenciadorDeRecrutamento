package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Perfil;

public record UsuarioCadastroDTO(
        String nome, // Nome do usuário interno.
        String email, // Identificação utilizada no login (por ser único).
        String senha, // Senha original recebida no cadastro.
        Perfil perfil // Define ADMIN ou RECRUTADOR.
) {}

/*
DTO apenas para entrada de dados.
Como contém senha, não deve retornar usuários em consultas.
 */
