package daos;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;

public class ChaveDAO {

    private final JdbcWrapper jdbcWrapper;

    public ChaveDAO(final JdbcWrapper jdbcWrapper) {
        this.jdbcWrapper = jdbcWrapper;
    }

    public BigDecimal carregar(final String tabela, final String coluna) throws Exception {
        BigDecimal valor = null;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select nvl(max(" + coluna + "),0) valor ");
        nativeSql.appendSql("  from " + tabela);

        final ResultSet resultSet = nativeSql.executeQuery();
        if (resultSet.next()) {
            valor = resultSet.getBigDecimal("valor");
        }

        return new BigDecimal((valor != null ? valor.longValue() : 0L) + 1L);
    }

    public BigDecimal carregar(final String tabela, final String coluna1, final BigDecimal vlrcol1, final String coluna2) throws Exception {
        BigDecimal valor = null;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select nvl(max(" + coluna2 + "),0) valor ");
        nativeSql.appendSql("  from " + tabela);
        nativeSql.appendSql(" where " + coluna1 + " = " + vlrcol1);
        
        final ResultSet resultSet = nativeSql.executeQuery();
        if (resultSet.next()) {
            valor = resultSet.getBigDecimal("valor");
        }

        return new BigDecimal((valor != null ? valor.longValue() : 0L) + 1L);
    }

    
    public BigDecimal geraTGFNUM(final String tabela, final String coluna) throws Exception {

    	BigDecimal valor = null;
    	
    	jdbcWrapper.openSession();
        
		final CallableStatement callableStatement = jdbcWrapper.getConnection()
				.prepareCall("{call STP_KEYGEN_TGFNUM(?, ?, ?, ?, ?, ?)}");
		
		callableStatement.setQueryTimeout(60);

		callableStatement.setString("p_arquivo", tabela);
		callableStatement.setInt("p_codemp", 1);
		callableStatement.setString("p_tabela", tabela);
		callableStatement.setString("p_campo", coluna);
		callableStatement.setInt("p_dsync", 0);
		callableStatement.registerOutParameter("p_ultcod", Types.DECIMAL);
		callableStatement.execute();

		valor = (BigDecimal) callableStatement.getObject("p_ultcod");
        return new BigDecimal((valor != null ? valor.longValue() : 0L) + 1L);
        
    }

    
    public BigDecimal carregarNumeroUnico(final BigDecimal orderId) throws Exception {
        BigDecimal nutipedido = null;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select nutiped ");
        nativeSql.appendSql("  from ad_tipedido ");
        nativeSql.appendSql("  where id_pedido_site = " + orderId.toString());

        final ResultSet resultSet = nativeSql.executeQuery();
        if (resultSet.next()) {
            nutipedido = resultSet.getBigDecimal("nutiped");
        }
        return nutipedido;
    }
    
    // recuperar chaveMD5 existente na tabela AD_FACILITACONS
    public boolean carregarChaveMD5(final String idChaveMD5) throws Exception {
    	
        boolean chave = false;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("SELECT MD5");
        nativeSql.appendSql("  FROM AD_FACILITACONS ");
        nativeSql.appendSql("  WHERE MD5 = '" + idChaveMD5.toString() + "'");

        final ResultSet resultSet = nativeSql.executeQuery();
        if (resultSet.next()) {
        	chave = resultSet.getString("MD5") != null ? true: false;
        }
        return chave;
    }  
    
}