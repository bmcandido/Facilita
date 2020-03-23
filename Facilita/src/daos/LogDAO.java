package daos;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import models.LogFacilita;

public class LogDAO {

    public static void log(final LogFacilita logFacilita) {
    	
    	System.out.println("Entrou LogFacilita");
    	
        final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = entityFacade.getJdbcWrapper();
        final ChaveDAO chaveDAO = new ChaveDAO(jdbcWrapper);
                
        Integer tamRotina = logFacilita.getRotina().length();
        Integer tamMensagem = logFacilita.getMensagem().length();
        
        if (tamRotina > 100) {
        	tamRotina = 100;
        }
           
        if (tamMensagem > 4000) {
        	tamMensagem = 4000;
        }


        try {
            
        	jdbcWrapper.openSession();

            // Cria value object para a instância do jape
            final DynamicVO dvoLog = (DynamicVO) entityFacade.getDefaultValueObjectInstance("AD_FACILITALOG");
            
            // Preenchimento com os dados do objeto
            final BigDecimal nulog = chaveDAO.carregar("AD_FACILITALOG", "NULOG");
            
            dvoLog.setProperty("NULOG", nulog);
            dvoLog.setProperty("TIPOLOG", logFacilita.getTipoLog());
            dvoLog.setProperty("ROTINA", logFacilita.getRotina().substring(0,tamRotina-1));
            dvoLog.setProperty("DHINC", Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            dvoLog.setProperty("STATUS_HTTP", logFacilita.getStatus());
            dvoLog.setProperty("MD5", logFacilita.getMD5());
            dvoLog.setProperty("TENTATIVA", logFacilita.getTentativa());
            dvoLog.setProperty("MENSAGEM", logFacilita.getMensagem().substring(0,tamMensagem-1));
            
            // Salva o registro no banco de dados
            entityFacade.createEntity("AD_FACILITALOG", (EntityVO) dvoLog);
            
            // Se Log for do tipo Erro, envia aviso de sistema para usuarios cadastrados
            if ((logFacilita.getTipoLog()=="ER") || (logFacilita.getTipoLog()=="CS" && 
            										 logFacilita.getStatus().doubleValue() != 200)) {
            	final DiversosDAO diversosDAO = new DiversosDAO(jdbcWrapper);
            	diversosDAO.enviaAvisos("FACILITA - LOGERRO", 
            							"Houve um problema no processamento da integração, verificar tela INTEGRAÇÃO FACILITA - LOGs.", 
            							BigDecimal.valueOf(3));
            }
            
            System.out.println("Gravou LogFacilita");
            
        } catch (final Exception exception) {
            exception.printStackTrace();
            
            System.out.println("Erro LogFacilita");
            
            StringWriter errors = new StringWriter();
            exception.printStackTrace(new PrintWriter(errors));

        } finally {
            JdbcWrapper.closeSession(jdbcWrapper);
        }
    }
}