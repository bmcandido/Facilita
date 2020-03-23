package daos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import models.Imoveis;
import models.Reservas;

public class DiversosDAO {
	
    private final JdbcWrapper jdbcWrapper;

    public DiversosDAO(JdbcWrapper jdbcWrapper) {
        this.jdbcWrapper = jdbcWrapper;
    }


    public BigDecimal consultaCorretor(final String cpf) throws Exception {

    	BigDecimal idCorretor = null;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        try {
			nativeSql.appendSql("select cor.corcodigo " + 
					            "  from tgfpar par," + 
					            "       timcor cor" + 
					            " where par.cgc_cpf = '" + cpf + "'" +
					            "   and par.codparc = cor.corparceiro");
			
			final ResultSet resultSet = nativeSql.executeQuery();
			if (resultSet.next()) {
				idCorretor = resultSet.getBigDecimal("corcodigo");
			}
		} catch (Exception e) {
			// Corretor não encontrado -- melhor men
			idCorretor = null;
		}

        return idCorretor;
    	
    }
	
    public BigDecimal consultaParceiro(final String cpfcnpj) throws Exception {

    	BigDecimal idParceiro = null;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        try {
			nativeSql.appendSql("select par.codparc " + 
					            "  from tgfpar par" +
					            " where par.cgc_cpf = '" + cpfcnpj + "'");
			
			final ResultSet resultSet = nativeSql.executeQuery();
			if (resultSet.next()) {
				idParceiro = resultSet.getBigDecimal("codparc");
			}
		} catch (Exception e) {
			// Parceiro Não Encontrado
			idParceiro = null;
		}

        return idParceiro;
    	
    }

    
    public BigDecimal consultaProspect(final String cpfcnpj) throws Exception {

    	BigDecimal idProspect = null;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        try {
			nativeSql.appendSql("select ppr.pprcodigo " + 
					            "  from timppr ppr" +
					            " where ppr.pprcpfcnpj = '" + cpfcnpj + "'");
			
			final ResultSet resultSet = nativeSql.executeQuery();
			if (resultSet.next()) {
				idProspect = resultSet.getBigDecimal("pprcodigo");
			}
		} catch (Exception e) {
			// Parceiro Não Encontrado
			idProspect = null;
		}

        return idProspect;
    	
    }
    
    public Imoveis consultaImovel(final Reservas reserva) throws Exception {

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        Imoveis imovel = new Imoveis();
        
        try {
			nativeSql.appendSql("select imv.imvcodigo," +
                                "       imv.imvtipodoimovel," +
                                "       imv.imvbairro," +
                                "       imv.imvedificio," +
                                "       imv.imvempreendimento" +
					            "  from ad_facilitaemp emp," +
                                "       timimv imv" +
					            " where emp.codprodfac = " + reserva.getCodproduto() +
					            "   and emp.eprcodigo = imv.imvempreendimento" +
					            "   and instr(replace(imv.imvdescricao,'-',''),replace('"+reserva.getNumero_unidade()+"','-','')) > 0 ");
			
			System.out.println(nativeSql.getSqlBuf().toString());
			
			final ResultSet resultSet = nativeSql.executeQuery();
			if (resultSet.next()) {
				
				System.out.println("Achou o imovel");
				
				imovel.setBairro(resultSet.getBigDecimal("imvbairro"));
				imovel.setCodigo(resultSet.getBigDecimal("imvcodigo"));
				imovel.setEdificio(resultSet.getBigDecimal("imvedificio"));
				imovel.setEmpreendimento(resultSet.getBigDecimal("imvempreendimento"));
				imovel.setTipoImovel(resultSet.getBigDecimal("imvtipodoimovel"));
				
			}
		} catch (Exception e) {
			// Imovel Não Encontrado
			System.out.println("Não encontrou o imovel, caiu na exceção");
			imovel = null;
		}

        return imovel;
    	
    }
    
    public Integer consultaImovelFAC(final BigDecimal imovel) throws Exception {

    	Integer qtde=0;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        	nativeSql.appendSql("select count(1) qtde" + 
					            "  from timipr ipr," + 
					            "       timimv imv" + 
					            " where ipr.iprimovel = " + imovel +  
					            "   and ipr.iprpropestagio in ('P', 'A')" + 
					            "   and ipr.iprimovel = imv.imvcodigo" + 
					            "   and imv.imvestagio in ('CP','RE')");
			
		final ResultSet resultSet = nativeSql.executeQuery();
		if (resultSet.next()) {
			qtde = resultSet.getInt("qtde");
		}
       	return qtde;	
   	
    }
    
    public BigDecimal consultaImovelReserva(final BigDecimal reserva) throws Exception {

    	BigDecimal imovel = BigDecimal.ZERO;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        	nativeSql.appendSql("select ipr.iprimovel" + 
					            "  from ad_facilitares res," + 
					            "       timipr ipr" +
					            " where res.nures = " + reserva +
					            "   and res.faccodigo = ipr.iprfac");
			
		final ResultSet resultSet = nativeSql.executeQuery();
		if (resultSet.next()) {
			imovel = resultSet.getBigDecimal("iprimovel");
		}
       	return imovel;	
   	
    }
    
    
    public BigDecimal cadastraProspect(final Reservas reserva) throws Exception {
        
    	final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
    	final ChaveDAO chaveDAO = new ChaveDAO(jdbcWrapper);
    	final SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	BigDecimal idProspect=null;
    	
    	
        // Cria value object para a instância do jape
        final DynamicVO dvoPPR = (DynamicVO) entityFacade.getDefaultValueObjectInstance("TimProspect");

    	System.out.println("Irá chamar metodo novoIdTabela");
    	idProspect = chaveDAO.carregar("TIMPPR", "PPRCODIGO");
    	
    	System.out.println("NovoId: "+idProspect);

    	
    	dvoPPR.setProperty("PPRCODIGO", idProspect);
    	dvoPPR.setProperty("PPRNOME", reserva.getNome_cliente());  // N: NEGOCIAÇÃO
    	
    	if (reserva.getCnpj()==null && reserva.getCpf()!=null) {
    		dvoPPR.setProperty("PPRTIPO", "F");
    		dvoPPR.setProperty("PPRCPFCNPJ", reserva.getCpf().replaceAll("[^0-9]", ""));
    	} else {
    		dvoPPR.setProperty("PPRTIPO", "J");
    		dvoPPR.setProperty("PPRCPFCNPJ", reserva.getCnpj().replaceAll("[^0-9]", ""));
    	}
    	
    	if (reserva.getSexo()==null) {
    		dvoPPR.setProperty("PPRSEXO", "M"); // campo obrigatorio
    	} else {
    		dvoPPR.setProperty("PPRSEXO", reserva.getSexo());
    	}

    	dvoPPR.setProperty("PPRTELEFONES", reserva.getTelefone());
    	dvoPPR.setProperty("PPREMAIL", reserva.getEmail());
//    	dvoPPR.setProperty("PPRUSUARIO", 0);
    	dvoPPR.setProperty("PPRDHALTER", Timestamp.valueOf(sdfDB.format(new Date())));
//    	dvoPPR.setProperty("PPRUSUALTER", 0);
    	dvoPPR.setProperty("PPRDHINCLUSAO", Timestamp.valueOf(sdfDB.format(new Date())));

        entityFacade.createEntity("TimProspect", (EntityVO) dvoPPR);
        
        System.out.println("Gerou registro do Prospect");
        return idProspect;

    }
    
	public void enviaAvisos(final String titulo,
							final String descricao,
							final BigDecimal importancia) throws Exception {

		final NativeSql nsUsuarios = new NativeSql(jdbcWrapper);
		final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        final ChaveDAO chaveDAO = new ChaveDAO(jdbcWrapper);
        final SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
        // Cria value object para a instância do jape
        final DynamicVO dvoAVIUSU = (DynamicVO) entityFacade.getDefaultValueObjectInstance("AvisoSistema");
		
		nsUsuarios.appendSql("select codusu from ad_facilitausuavi");

        try (ResultSet rsUsuarios = nsUsuarios.executeQuery()) {
        	
        	while(rsUsuarios.next()) {
        		
        		dvoAVIUSU.setProperty("NUAVISO", chaveDAO.geraTGFNUM("TSIAVI", "NUAVISO")); 
        		dvoAVIUSU.setProperty("TITULO", titulo);
        		dvoAVIUSU.setProperty("DESCRICAO", descricao);
        		dvoAVIUSU.setProperty("IDENTIFICADOR", "PERSONALIZADO");
        		dvoAVIUSU.setProperty("IMPORTANCIA", importancia);
        		dvoAVIUSU.setProperty("CODUSU", rsUsuarios.getBigDecimal("codusu"));
        		dvoAVIUSU.setProperty("CODGRUPO", null); 
        		dvoAVIUSU.setProperty("TIPO", "P");
        		dvoAVIUSU.setProperty("DHCRIACAO", Timestamp.valueOf(sdfDB.format(new Date())));
        		dvoAVIUSU.setProperty("CODUSUREMETENTE", BigDecimal.ZERO);        		
            	
                entityFacade.createEntity("AvisoSistema", (EntityVO) dvoAVIUSU);
                System.out.println("Gerou registro de aviso para usuário " + rsUsuarios.getBigDecimal("codusu").toString());
        		
        	}
       }
    }	
    

}
