package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model;

public enum StatusProcesso {
	INSCRITO, // Estágio inicial.
	TRIAGEM, // Análise do perfil do candidato (currículo).
	AVALIACAO, // Testes e dinâmicas.
	ENTREVISTA,
	APROVADO, // Candidato selecionado para a vaga, mas ainda não foi contratado.
	CONTRATADO, // Candidato efetivado formalmente.
	REPROVADO,
	DESISTIU; // Candidato desistiu do processo
}

/* Organização das etapas do processo:
Inicial: INSCRITO.
Intermediário: TRIAGEM, AVALIACAO, ENTREVISTA, APROVADO.
Final: CONTRATADO, REPROVADO, DESISTIU.
 */