package models;

import java.math.BigDecimal;
import java.util.Date;

public class Consultas {
	
	private  BigDecimal  codcons;                               
	private  String 	 tipo;                         
	private  String      md5;                         
	private  String      conteudo;                         
	private  Date        dhinc;                         
	private  String      situacao;                         
	private  String      logerro;
	
	
	public BigDecimal getCodcons() {
		return codcons;
	}
	public void setCodcons(BigDecimal codcons) {
		this.codcons = codcons;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	public Date getDhinc() {
		return dhinc;
	}
	public void setDhinc(Date dhinc) {
		this.dhinc = dhinc;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public String getLogerro() {
		return logerro;
	}
	public void setLogerro(String logerro) {
		this.logerro = logerro;
	}
	
}
