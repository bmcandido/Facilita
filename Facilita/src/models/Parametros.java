package models;

public class Parametros {
	
	
	private String     Token;
	private String     Instance;
	private String     EndPointReserva;
	private String     EndPointVenda;
	private Integer    Tentativas;
	
	public String getToken() {
		return Token;
	}
	public void setToken(String token) {
		Token = token;
	}
	public String getInstance() {
		return Instance;
	}
	public void setInstance(String instance) {
		Instance = instance;
	}
	public String getEndPointReserva() {
		return EndPointReserva;
	}
	public void setEndPointReserva(String endPointReserva) {
		EndPointReserva = endPointReserva;
	}
	public String getEndPointVenda() {
		return EndPointVenda;
	}
	public void setEndPointVenda(String endPointVenda) {
		EndPointVenda = endPointVenda;
	}
	public Integer getTentativas() {
		return Tentativas;
	}
	public void setTentativas(Integer tentativas) {
		Tentativas = tentativas;
	}
	
	
}
