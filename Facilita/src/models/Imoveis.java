package models;

import java.math.BigDecimal;

public class Imoveis {

	private BigDecimal   codigo;
	private BigDecimal   empreendimento;
	private BigDecimal   tipoImovel;
	private BigDecimal   bairro;
	private BigDecimal   edificio;
	
	public BigDecimal getCodigo() {
		return codigo;
	}
	public void setCodigo(BigDecimal codigo) {
		this.codigo = codigo;
	}
	public BigDecimal getEmpreendimento() {
		return empreendimento;
	}
	public void setEmpreendimento(BigDecimal empreendimento) {
		this.empreendimento = empreendimento;
	}
	public BigDecimal getTipoImovel() {
		return tipoImovel;
	}
	public void setTipoImovel(BigDecimal tipoImovel) {
		this.tipoImovel = tipoImovel;
	}
	public BigDecimal getBairro() {
		return bairro;
	}
	public void setBairro(BigDecimal bairro) {
		this.bairro = bairro;
	}
	public BigDecimal getEdificio() {
		return edificio;
	}
	public void setEdificio(BigDecimal edificio) {
		this.edificio = edificio;
	}
	
}
