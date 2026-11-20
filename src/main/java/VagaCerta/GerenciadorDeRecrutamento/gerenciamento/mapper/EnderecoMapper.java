package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.mapper;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.EnderecoDTO;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Endereco;

import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {

    // É compartilhado pelos Controllers de Candidato e Empresa.

    // Converte Endereco para EnderecoDTO.
    public EnderecoDTO toDTO(Endereco endereco) {

        return new EnderecoDTO(
                endereco.getId(),
                endereco.getCep(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getLogradouro(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado()
        );
    }

    // Converte EnderecoDTO para Endereco.
    public Endereco toEntity(EnderecoDTO dto) {

        Endereco endereco = new Endereco();

        endereco.setCep(dto.cep());
        endereco.setNumero(dto.numero());
        endereco.setComplemento(dto.complemento());
        endereco.setLogradouro(dto.logradouro());
        endereco.setBairro(dto.bairro());
        endereco.setCidade(dto.cidade());
        endereco.setEstado(dto.estado());

        return endereco;
    }
}
