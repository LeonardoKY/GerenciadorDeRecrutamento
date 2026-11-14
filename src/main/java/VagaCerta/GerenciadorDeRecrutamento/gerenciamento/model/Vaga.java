package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "Vaga", indexes = {@Index(name = "IDX_VAGA_EMPRESA", columnList = "EMPRESA_ID")})
public class Vaga {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "ID", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "DATA_ABERTURA", nullable = false, updatable = false)
	private LocalDateTime abertura;

	@Column(name = "DATA_FECHAMENTO", nullable = false, updatable = false)
	private LocalDateTime fechamento;

	@Column(name = "SALARIO_MENSAL", nullable = false, updatable = false)
	private double salarioMensal;

	@Enumerated(EnumType.STRING)
	@Column(name = "MODALIDADE", nullable = false, length = 20)
	private Modalidade modalidade;

	@Enumerated(EnumType.STRING)
	@Column(name = "SENIORIDADE", nullable = false, updatable = false, length = 20)
	private Senioridade senioridade;

	@Enumerated(EnumType.STRING)
	@Column(name = "AREA", nullable = false, updatable = false, length = 20)
	private AreaAtuacao area;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false, length = 10)
	private StatusVaga status;

	// Feedback: avaliação do cliente. Valor de 0 a 10
	@Column(name = "FEEDBACK", precision = 3,scale = 1)
	private float feedback;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_VAGA_EMPRESA"))
	private Empresa empresa;

	public Vaga() {}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public LocalDateTime getAbertura() {
		return abertura;
	}

	public void setAbertura(LocalDateTime abertura) {
		this.abertura = abertura;
	}

	public LocalDateTime getFechamento() {
		return fechamento;
	}

	public void setFechamento(LocalDateTime fechamento) {
		this.fechamento = fechamento;
	}

	public double getSalarioMensal() {
		return salarioMensal;
	}

	public void setSalarioMensal(double salarioMensal) {
		this.salarioMensal = salarioMensal;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}

	public Senioridade getSenioridade() {
		return senioridade;
	}

	public void setSenioridade(Senioridade senioridade) {
		this.senioridade = senioridade;
	}

	public AreaAtuacao getArea() {
		return area;
	}

	public void setArea(AreaAtuacao area) {
		this.area = area;
	}

	public StatusVaga getStatus() {
		return status;
	}

	public void setStatus(StatusVaga status) {
		this.status = status;
	}

	public float getFeedback() {
		return feedback;
	}

	public void setFeedback(float feedback) {
		this.feedback = feedback;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
}
