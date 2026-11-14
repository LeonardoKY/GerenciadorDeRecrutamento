package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(schema = "APP", name = "Empresa", uniqueConstraints = @UniqueConstraint(name = "UQ_EMPRESA_CNPJ", columnNames = "CNPJ"), indexes = {@Index(name = "IDX_EMPRESA", columnList = "EMPRESA"), @Index(name = "IDX_EMPRESA_CNPJ", columnList = "CNPJ")})
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "ID", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "EMPRESA", nullable = false, length = 50)
	private String empresa;

	@Column(name = "ANO_FUNDACAO",  nullable = false, updatable = false)
	private int anoFundacao;

	@Column(name = "QUANTIDADE_RECRUTADORES",  nullable = false)
	private int qtdeRecrutadores;

	@Column(name = "CLIENTES_ATIVOS")
	private int clientesAtivos;

	// CNPJ: possui 14 dígitos no formato XX.XXX.XXX/XXXX-XX
	// é único e não deve ser alterado
	@Column(name = "CNPJ",  nullable = false, length = 14, updatable = false)
	private String cnpj;

	@Enumerated(EnumType.STRING)
	@Column(name = "PORTE_EMPRESA", nullable = false,  length = 20)
	private PorteEmpresa porte;

	public Empresa() {}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
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
}
