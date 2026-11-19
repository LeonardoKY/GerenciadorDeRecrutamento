package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
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

	public StatusProcesso getStatus() {
		return status;
	}

	// Private: impede que outros componentes alterem o status sem passar pelo serviço.
	private void setStatus(StatusProcesso status) {
		this.status = status;
	}

	/* Método para transição de estado (regra de negócio).
	Transições permitidas entre os estados:
    Como o status do processo deve progredir de forma sequencial e linear,
    usaremos o Map para definir os estados liberados em cada etapa.
    Ex: TRIAGEM libera os estados AVALIACAO, REPROVADO e DESISTIU

    Obs.: CONTRATADO, REPROVADO e DESISTIU são estados finais.
     */
	public void atualizarStatus(StatusProcesso novoStatus) {

		Map<StatusProcesso, Set<StatusProcesso>> transicoes = Map.of(

				StatusProcesso.INSCRITO,
				Set.of(StatusProcesso.TRIAGEM,
						StatusProcesso.DESISTIU),

				StatusProcesso.TRIAGEM,
				Set.of(StatusProcesso.AVALIACAO,
						StatusProcesso.REPROVADO,
						StatusProcesso.DESISTIU),

				StatusProcesso.AVALIACAO,
				Set.of(StatusProcesso.ENTREVISTA,
						StatusProcesso.REPROVADO,
						StatusProcesso.DESISTIU),

				StatusProcesso.ENTREVISTA,
				Set.of(StatusProcesso.APROVADO,
						StatusProcesso.REPROVADO,
						StatusProcesso.DESISTIU),

				StatusProcesso.APROVADO,
				Set.of(StatusProcesso.CONTRATADO,
						StatusProcesso.DESISTIU)
		);

		Set<StatusProcesso> permitidos =
				transicoes.getOrDefault(status, Set.of());

		if (!permitidos.contains(novoStatus)) {
			throw new IllegalStateException(
					"Transição de " + status + " para " +
							novoStatus + " não permitida."
			);
		}

		setStatus(novoStatus);

		if (novoStatus == StatusProcesso.CONTRATADO ||
				novoStatus == StatusProcesso.REPROVADO ||
				novoStatus == StatusProcesso.DESISTIU) {

			this.encerramento = LocalDateTime.now();
		}
	}
}
