package models;

import java.math.BigDecimal;
import java.util.Date;

public class Reservas {

	private BigDecimal	nures;
	private BigDecimal  codnegocio;
	private String      nome_negocio;
	private BigDecimal  codproduto;
	private String      produto_empreendimento;                         
	private Date        criacao_negocio;                         
	private String      cpf;                         
	private String      cnpj;
	private BigDecimal  codunidade;
	private String      quadra_bloco;                         
	private String      numero_unidade;                         
	private String      nome_cliente;                         
	private String      sexo;                         
	private String      telefone;                         
	private String      celular;                         
	private String      email;                         
	private String      cpf_corretor;                         
	private String      status_reserva;                         
	private String      status_negociacao;                         
	private String      origens;                         
	private Date  	    atualizacao_negocio;                         
	private BigDecimal  valor_negocio_venda;                         
	private String      etapa_funil;                         
	private String      equipe;                         
	private String      nome_negocio_responsavel;                         
	private String      nome_gerente_negocio;                         
	private String      nome_corretor_negocio;                         
	private String      email_responsavel;                         
	private String      email_gerente;                         
	private String      email_corretor;                         
	private String      requisitou_reserva;    
	private Date		criacao_reserva;
	private BigDecimal  codcons;
	private String      logerro;	
	
	
	public BigDecimal getNures() {
		return nures;
	}
	public void setNures(BigDecimal nures) {
		this.nures = nures;
	}
	public BigDecimal getCodnegocio() {
		return codnegocio;
	}
	public void setCodnegocio(BigDecimal codnegocio) {
		this.codnegocio = codnegocio;
	}
	public String getNome_negocio() {
		return nome_negocio;
	}
	public void setNome_negocio(String nome_egocio) {
		this.nome_negocio = nome_egocio;
	}
	public String getProduto_empreendimento() {
		return produto_empreendimento;
	}
	public void setProduto_empreendimento(String produto_empreendimento) {
		this.produto_empreendimento = produto_empreendimento;
	}
	public Date getCriacao_negocio() {
		return criacao_negocio;
	}
	public void setCriacao_negocio(Date criacao_negocio) {
		this.criacao_negocio = criacao_negocio;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getQuadra_bloco() {
		return quadra_bloco;
	}
	public void setQuadra_bloco(String quadra_bloco) {
		this.quadra_bloco = quadra_bloco;
	}
	public String getNumero_unidade() {
		return numero_unidade;
	}
	public void setNumero_unidade(String numero_unidade) {
		this.numero_unidade = numero_unidade;
	}
	public String getNome_cliente() {
		return nome_cliente;
	}
	public void setNome_cliente(String nome_cliente) {
		this.nome_cliente = nome_cliente;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCpf_corretor() {
		return cpf_corretor;
	}
	public void setCpf_corretor(String cpf_corretor) {
		this.cpf_corretor = cpf_corretor;
	}
	public String getStatus_reserva() {
		return status_reserva;
	}
	public void setStatus_reserva(String status_reserva) {
		this.status_reserva = status_reserva;
	}
	public String getStatus_negociacao() {
		return status_negociacao;
	}
	public void setStatus_negociacao(String status_negociacao) {
		this.status_negociacao = status_negociacao;
	}
	public String getOrigens() {
		return origens;
	}
	public void setOrigens(String origens) {
		this.origens = origens;
	}
	public Date getAtualizacao_negocio() {
		return atualizacao_negocio;
	}
	public void setAtualizacao_negocio(Date atualizacao_negocio) {
		this.atualizacao_negocio = atualizacao_negocio;
	}
	public BigDecimal getValor_negocio_venda() {
		return valor_negocio_venda;
	}
	public void setValor_negocio_venda(BigDecimal valor_negocio_venda) {
		this.valor_negocio_venda = valor_negocio_venda;
	}
	public String getEtapa_funil() {
		return etapa_funil;
	}
	public void setEtapa_funil(String etapa_funil) {
		this.etapa_funil = etapa_funil;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getNome_negocio_responsavel() {
		return nome_negocio_responsavel;
	}
	public void setNome_negocio_responsavel(String nome_negocio_responsavel) {
		this.nome_negocio_responsavel = nome_negocio_responsavel;
	}
	public String getNome_gerente_negocio() {
		return nome_gerente_negocio;
	}
	public void setNome_gerente_negocio(String nome_gerente_negocio) {
		this.nome_gerente_negocio = nome_gerente_negocio;
	}
	public String getNome_corretor_negocio() {
		return nome_corretor_negocio;
	}
	public void setNome_corretor_negocio(String nome_corretor_negocio) {
		this.nome_corretor_negocio = nome_corretor_negocio;
	}
	public String getEmail_responsavel() {
		return email_responsavel;
	}
	public void setEmail_responsavel(String email_responsavel) {
		this.email_responsavel = email_responsavel;
	}
	public String getEmail_gerente() {
		return email_gerente;
	}
	public void setEmail_gerente(String email_gerente) {
		this.email_gerente = email_gerente;
	}
	public String getEmail_corretor() {
		return email_corretor;
	}
	public void setEmail_corretor(String email_corretor) {
		this.email_corretor = email_corretor;
	}
	public String getRequisitou_reserva() {
		return requisitou_reserva;
	}
	public void setRequisitou_reserva(String resquisitou_reserva) {
		this.requisitou_reserva = resquisitou_reserva;
	}
	public Date getCriacao_reserva() {
		return criacao_reserva;
	}
	public void setCriacao_reserva(Date criacao_reserva) {
		this.criacao_reserva = criacao_reserva;
	}
	public BigDecimal getCodcons() {
		return codcons;
	}
	public void setCodcons(BigDecimal codcons) {
		this.codcons = codcons;
	}
	public BigDecimal getCodproduto() {
		return codproduto;
	}
	public void setCodproduto(BigDecimal codproduto) {
		this.codproduto = codproduto;
	}
	public BigDecimal getCodunidade() {
		return codunidade;
	}
	public void setCodunidade(BigDecimal codunidade) {
		this.codunidade = codunidade;
	}
	public String getLogerro() {
		return logerro;
	}
	public void setLogerro(String logerro) {
		this.logerro = logerro;
	}  	
	
}
