package business;


import java.math.BigDecimal;
import java.util.List;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import daos.ChaveDAO;
import daos.DiversosDAO;
import daos.FacDAO;
import daos.ReservaDAO;
import models.Imoveis;
import models.Reservas;

public class IntegracaoFacilita {
	
    private final JdbcWrapper jdbcWrapper;

    public IntegracaoFacilita(JdbcWrapper jdbcWrapper) {
        this.jdbcWrapper = jdbcWrapper;
    }
	
    public void processaReservas() throws Exception {
    	
    	final ReservaDAO reservaDAO = new ReservaDAO(jdbcWrapper);
    	final DiversosDAO diversosDao = new DiversosDAO(jdbcWrapper);
    	final FacDAO facDAO = new FacDAO(jdbcWrapper);
    	
    	BigDecimal idParceiro=null; 
    	BigDecimal idProspect=null;
    	BigDecimal idCorretor=null;
    	BigDecimal idFAC=null;
    	
    	
        final List<Reservas> reservas = reservaDAO.carregarAProcessar();
        System.out.println("Carregou reservas pendentes");
        for (final Reservas reserva: reservas) {
        	
        	// VALIDAÇÕES
        	if (reserva.getCpf_corretor()==null) {
        		System.out.println("Reserva sem CPF do Corretor");
        		reservaDAO.marcarFalha(reserva, "Reserva sem CPF do Corretor");
        		continue;
        	} 
        	else if (reserva.getCpf()==null && reserva.getCnpj()==null) {
        		System.out.println("Reserva sem CPF ou CNPJ do Cliente");
        		reservaDAO.marcarFalha(reserva, "Reserva sem CPF ou CNPJ do Cliente");
        		continue;
        	}
        	
        	// OBTEM CODIGO DO CONSULTOR BASEADO NO CPF
        	System.out.println("Irá consultar corretor pelo CPF " + reserva.getCpf_corretor().replaceAll("[^0-9]", ""));
        	idCorretor = diversosDao.consultaCorretor(reserva.getCpf_corretor().replaceAll("[^0-9]", ""));
        	if (idCorretor==null) {
        		System.out.println("Não foi possível encontrar o corretor ou não há vinculo do mesmo com a tabela de Parceiros.");
        		reservaDAO.marcarFalha(reserva, "Não foi possível encontrar o corretor ou não há vinculo do mesmo com a tabela de Parceiros.");
        		continue;
        	}
        	
        	// VERIFICA SE EXISTE PARCEIRO OU PROSPECT CADASTRADOS
        	if (reserva.getCpf()==null) {
        		 System.out.println("Irá consultar idParceiro e idProspect CNPJ");
        		 idParceiro = diversosDao.consultaParceiro(reserva.getCnpj().replaceAll("[^0-9]", "")); 
        		 idProspect = diversosDao.consultaProspect(reserva.getCnpj().replaceAll("[^0-9]", ""));
        		 System.out.println("CNPJ: " + reserva.getCnpj());
        		 System.out.println("Consultou idParceiro e idProspect CNPJ");
        	} else {
        		 System.out.println("Irá consultar idParceiro e idProspect CPF");
        		 idParceiro = diversosDao.consultaParceiro(reserva.getCpf().replaceAll("[^0-9]", ""));
        		 idProspect = diversosDao.consultaProspect(reserva.getCpf().replaceAll("[^0-9]", ""));
        		 System.out.println("CPF: " + reserva.getCpf());
        		 System.out.println("Consultou idParceiro e idProspect CPF");
        	}
        	
        	
        	// CASO NÃO HAJA NEM PARCEIRO NEM PROSPECT CADASTRADOS, CADASTRA NOVO PROSPECT
        	if (idParceiro==null && idProspect==null) {
        		System.out.println("Irá cadastrar o prospect: " + reserva.getNome_cliente());
        		idProspect = diversosDao.cadastraProspect(reserva);
        	}
        	
        	
        	// ENCONTRA O IMOVEL BASEADO NAS INFORMAÇÕES DO CODIGO DO EMPREENDIMENTO(FACILITA) E UNIDADE
        	//OBTEM INFORMAÇÕES DO IMOVEL
        	Imoveis imovel = new Imoveis(); 
        	System.out.println("Irá consultar detalhes do imovel da reserva");
        	imovel = diversosDao.consultaImovel(reserva);
        	System.out.println("Retorno imovel: "+imovel.getCodigo());
        	if (imovel.getCodigo()==null) {
        		System.out.println("Não foi possível encontrar o imóvel no Sankhya W baseado nas informações do empreendimento e unidade recebidas.");
        		reservaDAO.marcarFalha(reserva, "Não foi possível encontrar o imóvel no Sankhya W baseado nas informações do empreendimento e unidade recebidas.");
        		continue;
        	} else if (diversosDao.consultaImovelFAC(imovel.getCodigo())>0) {
        		System.out.println("Imóvel já vinculado a uma FAC em estagio de Reserva ou Proposta.");
        		reservaDAO.marcarFalha(reserva, "Imóvel já vinculado a uma FAC em estagio de Reserva ou Proposta.");
        		continue;
        	}
        	
        	
        	// GRAVA CABEÇALHO DA FAC
        	try {
        		System.out.println("Ira gerar a FAC");
        		idFAC = facDAO.gravaFacDB(reserva, imovel, idCorretor, idProspect, idParceiro);
        		System.out.println("FAC: "+idFAC);
        		
        		// Se FAC foi gerada então prossegue etapas da reserva
        		if (idFAC.doubleValue()>0) {
        			// INDICA RESERVADO NAS TABELAS IMOVEL, FAC DO IMOVEL E LOTE
        			System.out.println("Ira reservar o imovel e lote");
        			reservaDAO.mudaStatusImovel(imovel.getCodigo(), "RE");
        			reservaDAO.mudaStatusLote(imovel.getCodigo(), "RE");
        			System.out.println("reservou o imovel e lote");
        			
        			// MARCA RESERVA COMO PROCESSADA
        			reservaDAO.marcarProcessado(reserva.getNures(), idFAC);
        		}
        		
        	 } catch (final Exception exception) {
        		 System.out.println("Erro 111: " + exception.getMessage());
        		 reservaDAO.marcarFalha(reserva, exception.getMessage());
        	}
        }
    }
 
    public void cancelaReservas() throws Exception {
    	
    	final ReservaDAO reservaDAO = new ReservaDAO(jdbcWrapper);
    	final DiversosDAO diversosDAO = new DiversosDAO(jdbcWrapper);
    	
        final List<BigDecimal> reservas = reservaDAO.carregarACancelar();
        System.out.println("Carregou reservas para cancelar");
        for (final BigDecimal reserva: reservas) {
        	
        	// GRAVA CABEÇALHO DA FAC
        	try {
        			// INDICA RESERVADO NAS TABELAS IMOVEL, FAC DO IMOVEL E LOTE
        			final BigDecimal imovel;
        			imovel = diversosDAO.consultaImovelReserva(reserva);
        			System.out.println("Ira cancelar o imovel e lote: "+imovel);
        			
        			reservaDAO.mudaStatusImovel(imovel, "DI");
        			reservaDAO.mudaStatusLote(imovel, "DI");
        			System.out.println("cancelou a reserva do imovel e lote");
        			
        			// MARCA RESERVA COMO PROCESSADA
        			reservaDAO.marcarCancelado(reserva);
        			
        	 } catch (final Exception exception) {
        		 
        		 System.out.println("Erro ao cancelar a reserva: " + exception.getMessage());
        		
        	}
        }
    }
    
    
    public void executaImportacao() throws Exception {
        final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = entityFacade.getJdbcWrapper();

        try {
            jdbcWrapper.openSession();
            
            System.out.println("Inicio Etapa 1");

            final ChaveDAO chaveDAO = new ChaveDAO(jdbcWrapper);
            System.out.println("Passou chaveDAO");
            
            final Importador importador = new Importador(entityFacade, chaveDAO);
            System.out.println("Passou Importador");

            // ETAPA 1: EXECUTA DESCIDA DE INFORMAÇÕES PARA TABELA AD_FACILITACONS (JSON)
            importador.consultaReservasWS();
            System.out.println("Passou consultaReservasWS");
            
            // ETAPA 2: ARMAZENA RESERVAS NA TABELA DE INTEGRAÇÃO AD_FACILITARES
            final ReservaDAO reservaDAO = new ReservaDAO(jdbcWrapper);
            reservaDAO.gravaReservasDB();
            System.out.println("Passou gravaReservasDB");
            

        } finally {
            JdbcWrapper.closeSession(jdbcWrapper);
            System.out.println("Fechou Sessão");
        }
    }
   
}