package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Genero;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.NivelFormacao;

import java.time.LocalDate;
import java.util.UUID;

public record CandidatoDTO(
        UUID id,
        String nome,
        String cpf,
        String email,
        LocalDate nascimento,
        int anosExperiencia,
        Genero genero,
        NivelFormacao formacao,
        EnderecoDTO endereco // O candidato possui apenas 1 endereço.
) {}
