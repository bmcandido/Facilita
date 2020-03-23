package business;

import java.sql.ResultSet;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import models.Parametros;

public class ObtemParametros {
	
	public static Parametros Carregar() throws Exception {

		final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
		final JdbcWrapper jdbcWrapper = entityFacade.getJdbcWrapper();
		final NativeSql nsParametros = new NativeSql(jdbcWrapper);
		
		Parametros paramRetorno=null;
		
        nsParametros.appendSql("SELECT P.TOKEN, P.INSTANCE, P.ENDPOINT_RESERVA, " + 
        		               "P.ENDPOINT_VENDA, P.TENTATIVAS " + 
        		               "FROM AD_FACILITAPARAM P " + 
        		               "WHERE P.NUPAR = 1");

        try (ResultSet rsParametros = nsParametros.executeQuery()) {
        	
        	while(rsParametros.next()) {

	            final Parametros parametros = new Parametros();
	            
	            parametros.setToken(rsParametros.getString("TOKEN"));
	            parametros.setInstance(rsParametros.getString("INSTANCE"));
	            parametros.setEndPointReserva(rsParametros.getString("ENDPOINT_RESERVA"));
	            parametros.setEndPointVenda(rsParametros.getString("ENDPOINT_VENDA"));
	            parametros.setTentativas(rsParametros.getInt("TENTATIVAS"));
	            
	            System.out.println("TOKEN: " + parametros.getToken());
	            System.out.println("INSTANCE: " + parametros.getInstance());
	            System.out.println("ENDPOINT_RESERVA: " + parametros.getEndPointReserva());
	            System.out.println("ENDPOINT_VENDA: " + parametros.getEndPointVenda());
	            System.out.println("TENTATIVAS: " + parametros.getTentativas().toString());

	            paramRetorno = parametros;
        	}
        } finally {
            JdbcWrapper.closeSession(jdbcWrapper);
        }
        
        return paramRetorno;
    }	

}

