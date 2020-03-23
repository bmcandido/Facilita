package business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import daos.ChaveDAO;
import daos.LogDAO;
import models.LogFacilita;
import models.Parametros;
import utils.Hash;
import utils.Http;
import utils.JSon;

class Importador {

    // Abrir conexao com o banco de dados
    private EntityFacade entityFacade;
    private ChaveDAO chaveDAO;

    Importador(final EntityFacade entityFacade, final ChaveDAO chaveDAO) {
        this.entityFacade = entityFacade;
        this.chaveDAO = chaveDAO;
    }

    void consultaReservasWS() throws Exception {
    	
    	System.out.println("Inicio Consulta Reservas");
    	
		new ObtemParametros();

		final LogFacilita log = new LogFacilita();
		final Parametros parametros = ObtemParametros.Carregar();
		Integer tentativa = 0;
		
    	final Map<String, String> headers = new HashMap<>();
        headers.put("facilita_token", parametros.getToken());
        headers.put("facilita_instance", parametros.getInstance());
        headers.put("Accept-Charset", "utf-8");
        
        System.out.println("Leu e atribuiu parametros");
        
        final Http httpArquivo = new Http(parametros.getEndPointReserva());
        Http.Response responseArquivo=null;
        
        // Efetua tentativa de conexão via http
    	tentativa = tentativa + 1;
        while (tentativa <= parametros.getTentativas()) {
        	System.out.println("*** TENTATIVA: " + tentativa);
        	try {
        	
        	    System.out.println("Tentativa: " + tentativa.toString());
	        	responseArquivo = httpArquivo.get(headers);
	        	System.out.println("*** PASSOU 36");
	        	break;
	        	
            } catch (final Exception exception) {
                // Gera registro de LOG
            	System.out.println("*** PASSOU 37");
                log.setTipoLog("CS");
                log.setRotina("Importador.consultaReservas");
                log.setStatus(BigDecimal.valueOf(0L));
                log.setTentativa(BigDecimal.valueOf((long) tentativa));
                log.setMensagem("*** Host Inacessivel. ***");
                LogDAO.log(log);
            	
            	TimeUnit.SECONDS.sleep(60); //  segundos até proxima tentativa 
            	tentativa = tentativa + 1;;
  	    		System.out.println("Tentativa: " + tentativa.toString());
    	    		
        	}
        	
      	}

        System.out.println("*** PASSOU 38");
        // Caso exceda a quantidade de tentativas, interrompe o fluxo de execução
        if (tentativa > parametros.getTentativas()) {
           System.out.println("Abotado pela tentativa: " + tentativa.toString());
           return;
        }
        
    	if (responseArquivo.getCode() != 200) {
            // Gera registro de LOG
            log.setTipoLog("CS");
            log.setRotina("Importador.consultaReservas");
            log.setStatus(BigDecimal.valueOf(responseArquivo.getCode()));
            log.setTentativa(BigDecimal.valueOf((long) tentativa));
            log.setMensagem(responseArquivo.getMessage());
            LogDAO.log(log);
            return;
    	}        
        
        String retornoArquivo = new String(responseArquivo.getData(), "utf-8");
        
        System.out.println("Retorno Arquivo: " + retornoArquivo);
        
        // Não faz nada caso conteúdo esteja vazio

        System.out.println(retornoArquivo.toString().trim());
//        if (retornoArquivo.toString().trim() == "[]") {
//        	return;
//        }

        // Cria value object para a instância do jape
        final DynamicVO dvoArquivo = (DynamicVO) entityFacade.getDefaultValueObjectInstance("AD_FACILITACONS");

        // criar hash md5 
		String chaveMD5 = Hash.md5(retornoArquivo);
		
		System.out.println("chaveMD5: " + chaveMD5);
		
        // Gera registro de LOG
        log.setTipoLog("CS");
        log.setRotina("Importador.consultaReservas");
        log.setStatus(BigDecimal.valueOf(responseArquivo.getCode()));
        log.setMensagem(responseArquivo.getMessage());
        log.setMD5(chaveMD5);
        LogDAO.log(log);  

		
        // consultar no db pela chave
//		boolean existeMD5 = chaveDAO.carregarChaveMD5(chaveMD5);
		// existindo a chave na tabela, interromper a execução o arquivo json já está gravado   
//		if (existeMD5){	return; }
		
        // Preenchimento com os dados do objeto
        final BigDecimal codcons = chaveDAO.carregar("AD_FACILITACONS", "CODCONS");
        
        System.out.println("codcons: " + codcons.toString());
        
        // Ajusta JSon retornado pelo facilita. No momento 04/10/2019 esta começando e finalizando com colchetes
        // como se o conteudo principal estivesse em uma grande lista. Deste modo o JSOn fica mal formado. Deve estar entre {}
        retornoArquivo = JSon.ajustaJsonFacilita(retornoArquivo);        
        
        dvoArquivo.setProperty("CODCONS", codcons);
        dvoArquivo.setProperty("TIPO", "RE");
        dvoArquivo.setProperty("CONTEUDO", retornoArquivo.toCharArray());
        dvoArquivo.setProperty("SITUACAO", "PE");
        dvoArquivo.setProperty("DHINC", Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        // gravar chave md5 na tabela caso chave não exista
        dvoArquivo.setProperty("MD5", chaveMD5);
        
        // Salva o registro no banco de dados
        entityFacade.createEntity("AD_FACILITACONS", (EntityVO) dvoArquivo);
        
        System.out.println("Gravou consulta " + codcons.toString());
        
        final JsonElement jeArquivo = new JsonParser().parse(retornoArquivo);
        final JsonObject joArquivo = jeArquivo.getAsJsonObject();
        
        System.out.println("sucess: " + joArquivo.get("success"));

        final int sucessoArquivo = joArquivo.get("success").getAsInt();
        if (sucessoArquivo != 1) {
        	
            log.setTipoLog("CS");
            log.setRotina("Importador.consultaReservas");
            log.setStatus(BigDecimal.valueOf(responseArquivo.getCode()));
            log.setMensagem("Consulta ao Webservice não retornou código interno sucesso 1.");
            log.setMD5(chaveMD5);
            LogDAO.log(log);  
        } 
    }
    
}