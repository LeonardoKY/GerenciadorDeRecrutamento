package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "Empresa", uniqueConstraints = @UniqueConstraint(name = "UQ_EMPRESA_CNPJ", columnNames = "CNPJ"), indexes = {@Index(name = "IDX_EMPRESA_NOME", columnList = "NOME"), @Index(name = "IDX_EMPRESA_CNPJ", columnList = "CNPJ")})
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "ID", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "NOME", nullable = false, length = 50)
	private String nome;

	@Column(name = "ANO_FUNDACAO",  nullable = false)
	private int anoFundacao;

	@Column(name = "QUANTIDADE_RECRUTADORES",  nullable = false)
	private int qtdeRecrutadores;

	@Column(name = "CLIENTES_ATIVOS")
	private int clientesAtivos;

	// CNPJ: possui 14 dígitos no formato XX.XXX.XXX/XXXX-XX
	// É único e não deve ser alterado.
	@Column(name = "CNPJ",  nullable = false, length = 14)
	private String cnpj;

	@Enumerated(EnumType.STRING)
	@Column(name = "PORTE_EMPRESA", nullable = false,  length = 20)
	private PorteEmpresa porte;

	/* Relacionamento Bidirecional entre Empresa e Endereço

	@OneToMany: a empresa pode possuir vários endereços. Cada endereço só pode possuir um proprietário.

	mappedBy: o atributo "empresa" da classe "Endereco" controla o relacionamento.

	CascadeType.ALL: as operações de persistência são propagadas para os endereços associados.

	orphanRemoval = true: exclui o endereço do banco quando ele é removido da coleção da empresa.
		Mantém a consistência do banco de dados e a sincronia entre Empresa e Endereco;
	 */
	@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
	// Obs.: @JoinColumn já está declarado na classe Endereco.
	private Collection<Endereco> enderecos = new ArrayList<>();

	public Empresa() {}

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

	public int getAnoFundacao() {
		return anoFundacao;
	}

	public void setAnoFundacao(int anoFundacao) {
		this.anoFundacao = anoFundacao;
	}

	public int getQtdeRecrutadores() {
		return qtdeRecrutadores;
	}

	public void setQtdeRecrutadores(int qtdeRecrutadores) {
		this.qtdeRecrutadores = qtdeRecrutadores;
	}

	public int getClientesAtivos() {
		return clientesAtivos;
	}

	public void setClientesAtivos(int clientesAtivos) {
		this.clientesAtivos = clientesAtivos;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public PorteEmpresa getPorte() {
		return porte;
	}

	public void setPorte(PorteEmpresa porte) {
		this.porte = porte;
	}

	// Métodos para manter a sincronização do relacionamento na memória.

	public void adicionarEndereco(Endereco endereco) {
		enderecos.add(endereco);
		endereco.setEmpresa(this);
	}

	public void removerEndereco(Endereco endereco) {
		if (enderecos.remove(endereco)) {
			endereco.setEmpresa(null);
		}
	}
}
