package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model;

import jakarta.persistence.*;
import org.hibernate.annotations.processing.Find;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "Candidato", uniqueConstraints = {@UniqueConstraint(name = "UQ_CANDIDATO_CPF", columnNames = "CPF"), @UniqueConstraint(name = "UQ_CANDIDATO_EMAIL", columnNames = "EMAIL")}, indexes = {@Index(name = "IDX_CANDIDATO_NOME", columnList = "NOME"), @Index(name = "IDX_CANDIDATO_CPF", columnList = "CPF")})
public class Candidato {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "ID", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "NOME",  nullable = false, length = 50, updatable = false)
	private String nome;

	// cpf contém 11 dígitos. Formato: XXX.XXX.XXX-XX
	@Column(name = "CPF", nullable = false, length = 11, updatable = false)
	private String cpf;

	@Column(name = "EMAIL", nullable = false, length = 50)
	private String email;

	@Column(name = "NASCIMENTO", nullable = false, updatable = false)
	private LocalDate nascimento;

	@Column(name = "EXPERIENCIA")
	private int anosExperiencia;

	@Enumerated(EnumType.STRING)
	@Column(name = "GENERO", nullable = false, updatable = false, length = 20)
	private Genero genero;

	@Enumerated(EnumType.STRING)
	@Column(name = "FORMACAO", nullable = false, length = 20)
	private NivelFormacao formacao;

	public Candidato() {}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getNascimento() {
		return nascimento;
	}

	public void setNascimento(LocalDate nascimento) {
		this.nascimento = nascimento;
	}

	public int getAnosExperiencia() {
		return anosExperiencia;
	}

	public void setAnosExperiencia(int anosExperiencia) {
		this.anosExperiencia = anosExperiencia;
	}

	public Genero getGenero() {
		return genero;
	}

	public void setGenero(Genero genero) {
		this.genero = genero;
	}

	public NivelFormacao getFormacao() {
		return formacao;
	}

	public void setFormacao(NivelFormacao formacao) {
		this.formacao = formacao;
	}
}
