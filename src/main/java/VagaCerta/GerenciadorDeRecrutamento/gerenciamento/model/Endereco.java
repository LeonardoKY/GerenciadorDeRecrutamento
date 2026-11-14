package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.util.UUID;

@Entity
@Table(schema = "APP", name = "Endereco",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_ENDERECO_CANDIDATO", columnNames = "CANDIDATO_ID")
        },
        indexes = {
                @Index(name = "UQ_ENDERECO_EMPRESA", columnList = "EMPRESA_ID"),
                @Index(name = "UQ_ENDERECO_CANDIDATO", columnList = "CANDIDATO_ID")
        }
)

// Anotação do Hibernate: gera uma restrição no banco de dados.
// O Check garante que o endereco pertença exclusivamente à empresa ou ao candidato.
@Check(
        constraints = """
                (EMPRESA_ID IS NOT NULL AND CANDIDATO_ID IS NULL)
                OR
                (EMPRESA_ID IS NULL AND CANDIDATO_ID IS NOT NULL)
                """
)

public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    // O cep é composto por 8 dígitos
    @Column(name = "CEP", nullable = false, length = 8)
    private String cep;

    @Column(name = "NUMERO", nullable = false)
    private int numero;

    @Column(name = "COMPLEMENTO", nullable = false, length = 50)
    private String complemento;

    @Column(name = "LOGRADOURO", nullable = false, length = 50)
    private String logradouro;

    @Column(name = "BAIRRO", nullable = false, length = 50)
    private String bairro;

    @Column(name = "CIDADE", nullable = false, length = 50)
    private String cidade;

    @Column(name = "ESTADO", nullable = false, length = 50)
    private String estado;

	/*
	endereco pertence apenas à empresa ou ao candidato e não aos dois simultaneamente.
	endereco não pode ficar sem um proprietário (deve pertencer à alguém).
	unique garante que um candidato não tenha dois endereços associados.
	Uma empresa pode ter 1 ou mais enderecos.
	 */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPRESA_ID", foreignKey = @ForeignKey(name = "FK_ENDERECO_EMPRESA"))
    private Empresa empresa;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CANDIDATO_ID", foreignKey = @ForeignKey(name = "FK_ENDERECO_CANDIDATO"))
    private Candidato candidato;

    public Endereco() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }
}
