package daos;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import models.Consultas;

public class ConsultaDAO {
	
    private final JdbcWrapper jdbcWrapper;

    public ConsultaDAO(final JdbcWrapper jdbcWrapper) {
        this.jdbcWrapper = jdbcWrapper;
    }


	public List<Consultas> carregaPendentes() throws Exception {

		final NativeSql nsConsultas = new NativeSql(jdbcWrapper);
		
		nsConsultas.appendSql("select codcons, tipo, conteudo, situacao," +
					          "       dhinc, md5, logerro" +
       		                  "  from ad_facilitacons " + 
       		                  " where situacao = 'PE'" + 
       		                  "   and logerro is null" +
       		                  " order by codcons");

        try (ResultSet rsConsultas = nsConsultas.executeQuery()) {
        	
        	final List<Consultas> consultas = new ArrayList<>();
        	
        	while(rsConsultas.next()) {
        		
        		final Consultas consulta = new Consultas();
        		
        		consulta.setCodcons(rsConsultas.getBigDecimal("codcons"));
        		consulta.setTipo(rsConsultas.getString("tipo"));
        		consulta.setConteudo(rsConsultas.getString("conteudo"));
        		consulta.setSituacao(rsConsultas.getString("situacao"));
        		consulta.setDhinc(rsConsultas.getDate("dhinc"));
        		consulta.setMd5(rsConsultas.getString("md5"));
        		consulta.setLogerro(rsConsultas.getString("logerro"));

	            consultas.add(consulta);
	            
        	}
            return consultas;
       }
    }	
	
    public void marcarProcessado(final BigDecimal codcons) throws SQLException {
        final String sql = "" +
                "begin " +
                "  update ad_facilitacons" +
                "     set situacao = 'PR'" +
                "   where codcons = :codcons;" +
                "end;";

        final Connection connection = jdbcWrapper.getConnection();
        try (final CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setBigDecimal("codcons", codcons);

            callableStatement.execute();
        }
    }
    
    public void insereNegocioConsulta(final BigDecimal codConsulta,
 									  final BigDecimal codProdFacilita, 
 								      final BigDecimal codNegocioFacilita,
									  final BigDecimal codUnidadeFacilita,
									  final String tipoConsulta) throws Exception {
        
    	final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
    	
        // Cria value object para a instância do jape
        final DynamicVO dvoNEG = (DynamicVO) entityFacade.getDefaultValueObjectInstance("AD_FACILITANEGOC");
        
        dvoNEG.setProperty("CODCONS", codConsulta);
        dvoNEG.setProperty("CODPRODFACILITA", codProdFacilita);
        dvoNEG.setProperty("CODNEGOCIOFACILITA", codNegocioFacilita);
        dvoNEG.setProperty("CODUNIDADEFACILITA", codUnidadeFacilita);
        dvoNEG.setProperty("TIPO", tipoConsulta);
    	
        entityFacade.createEntity("AD_FACILITANEGOC", (EntityVO) dvoNEG);
        
        System.out.println("Gerou registro Negociação na Consulta");

    }
	
}
