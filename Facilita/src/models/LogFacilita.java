package models;

import java.math.BigDecimal;

public class LogFacilita {

	private String      TipoLog;
	private String   	Rotina;
    private BigDecimal  Status;
    private String   	MD5;
    private BigDecimal  Tentativa;
    private String   	Mensagem;

    public String getTipoLog() {
		return TipoLog;
	}
	public void setTipoLog(String tipoLog) {
		TipoLog = tipoLog;
	}
	public String getRotina() {
		return Rotina;
	}
	public void setRotina(String rotina) {
		Rotina = rotina;
	}
	public BigDecimal getStatus() {
		return Status;
	}
	public void setStatus(BigDecimal status) {
		Status = status;
	}
	public String getMD5() {
		return MD5;
	}
	public void setMD5(String mD5) {
		MD5 = mD5;
	}
	public BigDecimal getTentativa() {
		return Tentativa;
	}
	public void setTentativa(BigDecimal tentativa) {
		Tentativa = tentativa;
	}
	public String getMensagem() {
		return Mensagem;
	}
	public void setMensagem(String mensagem) {
		Mensagem = mensagem;
	}
}
