package actions;

import java.io.PrintWriter;
import java.io.StringWriter;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import business.IntegracaoFacilita;
import daos.LogDAO;
import models.LogFacilita;

public class BotaoIntegracao implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contextoAcao) {
    	
        final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = entityFacade.getJdbcWrapper();
    	
    	
        try {

        	System.out.println("Inicio Botão Integração");
        	
            jdbcWrapper.openSession();

           final IntegracaoFacilita integracaoFacilita = new IntegracaoFacilita(jdbcWrapper);
        	
        	// 1- Consulta Reservas e Vendas na API do Facilita e armazena em Tabelas Locais
           integracaoFacilita.executaImportacao();
            
            System.out.println("Após executaImportacao");
            
            // Processa informações das Reservas e Vendas criando ou atualizando registros na FAC
            integracaoFacilita.processaReservas();
            System.out.println("Passou processaReservas");
            
            // Cancela reservas
            integracaoFacilita.cancelaReservas();
            System.out.println("Passou cancelaReservas");


            contextoAcao.setMensagemRetorno("Processamento Concluído.");

        } catch (final Exception exception) {
            exception.printStackTrace();
            
            StringWriter errors = new StringWriter();
            exception.printStackTrace(new PrintWriter(errors));
            
            // Gera registro de LOG
            final LogFacilita log = new LogFacilita();
            log.setTipoLog("ER");
            log.setRotina("BotaoIntegracao.doAction");
            log.setMensagem( errors.toString());
            LogDAO.log(log);       
            
        } finally {
        	
        	JdbcWrapper.closeSession(jdbcWrapper);
           
        }
    }
}