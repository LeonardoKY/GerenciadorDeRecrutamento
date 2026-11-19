package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/*
O candidato não pode se inscrever para a mesma vaga duas vezes, ou seja,
a combinação entre CANDIDATO_ID e VAGA_ID devem ser únicas.
A configuração @UniqueConstraint do banco de dados não permite duplicação.
 */
@Entity
@Table(schema = "APP" ,name = "Processo", uniqueConstraints = @UniqueConstraint(name = "UQ_PROCESSO_CANDIDATO_VAGA", columnNames = {"CANDIDATO_ID", "VAGA_ID"}), indexes = {@Index(name = "IDX_PROCESSO_CANDIDATO", columnList = "CANDIDATO_ID"), @Index(name = "IDX_PROCESSO_VAGA", columnList = "VAGA_ID")})
public class Processo {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "ID", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "PRETENCAO_SALARIAL",nullable = false)
	private double pretensaoSalarial;

	// Data que foi realizada a inscrição do processo
	@Column(name = "INSCRICAO", nullable = false)
	private LocalDateTime inscricao;

	// Data de encerramento do processo
	@Column(name = "ENCERRAMENTO")
	private LocalDateTime encerramento;
	// Data de encerramento nula: o processo não foi finalizado e não há uma data de término prevista

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS",  nullable = false)
	private StatusProcesso status = StatusProcesso.INSCRITO; // o status do processo inicia em INSCRITO.

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CANDIDATO_ID", nullable = false)
	private Candidato candidato;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAGA_ID",  nullable = false)
	private Vaga vaga;

	public Processo() {}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public double getPretensaoSalarial() {
		return pretensaoSalarial;
	}

	public void setPretensaoSalarial(double pretensaoSalarial) {
		this.pretensaoSalarial = pretensaoSalarial;
	}

	public LocalDateTime getInscricao() {
		return inscricao;
	}

	public void setInscricao(LocalDateTime inscricao) {
		this.inscricao = inscricao;
	}

	public LocalDateTime getEncerramento() {
		return encerramento;
	}

	public void setEncerramento(LocalDateTime encerramento) {
		this.encerramento = encerramento;
	}

	public StatusProcesso getStatus() {
		return status;
	}

	public void setStatus(StatusProcesso status) {
		this.status = status;
	}

	public Candidato getCandidato() {
		return candidato;
	}

	public void setCandidato(Candidato candidato) {
		this.candidato = candidato;
	}

	public Vaga getVaga() {
		return vaga;
	}

	public void setVaga(Vaga vaga) {
		this.vaga = vaga;
	}
}
