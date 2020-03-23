package daos;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;

import java.math.BigDecimal;
import java.sql.ResultSet;

public class PreferenciaDAO {

    private JdbcWrapper jdbcWrapper;
    
    // Chamada exemplo
    // final String url = preferenciaDao.getString("URLINTIBCFIN");

    public PreferenciaDAO(final JdbcWrapper jdbcWrapper) {
        this.jdbcWrapper = jdbcWrapper;
    }

    public String getString(final String chave) throws Exception {
        String parametro = null;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select texto ");
        nativeSql.appendSql("  from tsipar ");
        nativeSql.appendSql("  where chave = '" + chave + "'");

        final ResultSet resultSet = nativeSql.executeQuery();
        if (resultSet.next()) {
            parametro = resultSet.getString("texto");
        }

        return parametro;
    }

    public BigDecimal getBigDecimal(final String chave) throws Exception {
        BigDecimal parametro = null;

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select inteiro ");
        nativeSql.appendSql("  from tsipar ");
        nativeSql.appendSql("  where chave = '" + chave + "'");

        final ResultSet resultSet = nativeSql.executeQuery();

        if (resultSet.next()) {
            parametro = resultSet.getBigDecimal("inteiro");
        }

        return parametro;
    }
}
