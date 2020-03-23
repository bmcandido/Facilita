package daos;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import models.Consultas;
import models.Reservas;
import utils.JSon;

public class ReservaDAO {
	
    private final JdbcWrapper jdbcWrapper;

    public ReservaDAO(final JdbcWrapper jdbcWrapper) {
        this.jdbcWrapper = jdbcWrapper;
    }
    
    
	public void gravaReservasDB() throws Exception {
    	
        final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
		
		SimpleDateFormat sdfJSON = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        final ConsultaDAO consultaDAO = new ConsultaDAO(jdbcWrapper);    	
        final ChaveDAO chaveDAO = new ChaveDAO(jdbcWrapper);
    	
    	final List<Consultas> listConsultas = consultaDAO.carregaPendentes();
    	for (final Consultas consulta : listConsultas) {

    		final JsonElement jeArquivo = new JsonParser().parse(consulta.getConteudo());
            final JsonObject joArquivo = jeArquivo.getAsJsonObject();
            
            final int sucessoArquivo = joArquivo.get("success").getAsInt();
            System.out.println("sucessoArquivo: " + sucessoArquivo);
            if (sucessoArquivo == 1) {

                final JsonArray jaConsultas = joArquivo.getAsJsonArray("data");
                for (final JsonElement jeConsulta : jaConsultas) {
                	
                    final JsonObject joReserva = jeConsulta.getAsJsonObject();
                    
                    // Insere chave Negocio
                    consultaDAO.insereNegocioConsulta(consulta.getCodcons(), 
                    		                          JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_produto"), 
                    		                          JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_negocio"), 
                    		                          JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_unidade"),
                    		                          consulta.getTipo());
                    
                    // Verifica se negocio ja foi cadastrado como reserva
                    if (verificaNegocioExiste(JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_produto"), 
         								 	  JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_negocio"), 
         									  JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_unidade")) > 0) {
                       continue; 
         			}
                    
                    // Cria value object para a instância do jape
                    final DynamicVO dvoReserva = (DynamicVO) entityFacade.getDefaultValueObjectInstance("AD_FACILITARES");

                    dvoReserva.setProperty("NURES", chaveDAO.carregar("AD_FACILITARES", "NURES"));
                    
                    dvoReserva.setProperty("CODNEGOCIO_FACILITA",JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_negocio"));
                    dvoReserva.setProperty("NOME_NEGOCIO", JSon.getAsStringFromJsonObject(joReserva, "nome_negocio"));
                    
                    
                    Date dtCriacaoNegocio = sdfJSON.parse(JSon.getAsStringFromJsonObject(joReserva, "criacao_negocio"));
                    dvoReserva.setProperty("CRIACAO_NEGOCIO", Timestamp.valueOf(sdfDB.format(dtCriacaoNegocio)));
                    
//                    dvoReserva.setProperty("ORIGENS", JSon.getAsStringFromJsonObject(joReserva, "origens"));
                    dvoReserva.setProperty("NOME_CLIENTE", JSon.getAsStringFromJsonObject(joReserva, "nome_cliente"));
                    dvoReserva.setProperty("CPF", JSon.getAsStringFromJsonObject(joReserva, "cpf"));                    
                    dvoReserva.setProperty("CNPJ", JSon.getAsStringFromJsonObject(joReserva, "cnpj"));
                    
                    // Sempre criar os tipos na tebale com as duas primeiras letas
                    System.out.println("["+JSon.getAsStringFromJsonObject(joReserva, "sexo")+"]");
                    if (!JSon.getAsStringFromJsonObject(joReserva, "sexo").isEmpty()) {
                        dvoReserva.setProperty("SEXO", JSon.getAsStringFromJsonObject(joReserva, "sexo").substring(0, 1).toUpperCase());
                    } else {
                    	dvoReserva.setProperty("SEXO", JSon.getAsStringFromJsonObject(joReserva, "sexo"));
                    }
                    
                    dvoReserva.setProperty("TELEFONE", JSon.getAsStringFromJsonObject(joReserva, "telefone"));
                    dvoReserva.setProperty("CELULAR", JSon.getAsStringFromJsonObject(joReserva, "celular"));
                    dvoReserva.setProperty("EMAIL", JSon.getAsStringFromJsonObject(joReserva, "email"));

                    Date dtAtualizacaoNegocio = sdfJSON.parse(JSon.getAsStringFromJsonObject(joReserva, "atualizacao_negocio"));
                    dvoReserva.setProperty("ATUALIZACAO_NEGOCIO", Timestamp.valueOf(sdfDB.format(dtAtualizacaoNegocio)));
                    
                    dvoReserva.setProperty("VALOR_NEGOCIO_VENDA", JSon.getAsBigDecimalFromJsonObject(joReserva, "valor_negocio_venda"));
                    dvoReserva.setProperty("ETAPA_FUNIL", JSon.getAsStringFromJsonObject(joReserva, "etapa_funil"));
                    dvoReserva.setProperty("STATUS_NEGOCIACAO", JSon.getAsStringFromJsonObject(joReserva, "status_negociacao"));
                    dvoReserva.setProperty("EQUIPE", JSon.getAsStringFromJsonObject(joReserva, "equipe"));
                    dvoReserva.setProperty("NOME_NEGOCIO_RESPONSAVEL", JSon.getAsStringFromJsonObject(joReserva, "nome_negocio_responsavel"));
                    dvoReserva.setProperty("NOME_GERENTE_NEGOCIO", JSon.getAsStringFromJsonObject(joReserva, "nome_gerente_negocio"));
                    dvoReserva.setProperty("NOME_CORRETOR_NEGOCIO", JSon.getAsStringFromJsonObject(joReserva, "nome_corretor_negocio"));
                    dvoReserva.setProperty("CPF_CORRETOR", JSon.getAsStringFromJsonObject(joReserva, "cpf_corretor"));
                    dvoReserva.setProperty("EMAIL_RESPONSAVEL", JSon.getAsStringFromJsonObject(joReserva, "email_responsavel"));
                    dvoReserva.setProperty("EMAIL_GERENTE", JSon.getAsStringFromJsonObject(joReserva, "email_gerente"));
                    dvoReserva.setProperty("EMAIL_CORRETOR", JSon.getAsStringFromJsonObject(joReserva, "email_corretor"));
                    dvoReserva.setProperty("RESQUISITOU_RESERVA", JSon.getAsStringFromJsonObject(joReserva, "resquisitou_reserva"));
                    dvoReserva.setProperty("CODPROD_FACILITA",JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_produto"));
                    dvoReserva.setProperty("PRODUTO_EMPREENDIMENTO", JSon.getAsStringFromJsonObject(joReserva, "produto_empreendimento"));
                    dvoReserva.setProperty("CODUNIDADE_FACILITA",JSon.getAsBigDecimalFromJsonObject(joReserva, "codigo_unidade"));
                    dvoReserva.setProperty("QUADRA_BLOCO", JSon.getAsStringFromJsonObject(joReserva, "quadra_bloco"));
                    dvoReserva.setProperty("NUMERO_UNIDADE", JSon.getAsStringFromJsonObject(joReserva, "numero_unidade"));
                    
                    // Sempre criar os tipos na tebale com as duas primeiras letas
                    if (!JSon.getAsStringFromJsonObject(joReserva, "status_reserva").isEmpty()) {
                        dvoReserva.setProperty("STATUS_RESERVA", JSon.getAsStringFromJsonObject(joReserva, "status_reserva").substring(0, 2).toUpperCase());
                    } else {
                    	dvoReserva.setProperty("STATUS_RESERVA", JSon.getAsStringFromJsonObject(joReserva, "status_reserva"));
                    }

                    Date dtCriacaoReserva = sdfJSON.parse(JSon.getAsStringFromJsonObject(joReserva, "criacao_reserva"));
                    dvoReserva.setProperty("CRIACAO_RESERVA", Timestamp.valueOf(sdfDB.format(dtCriacaoReserva)));
                    
                    dvoReserva.setProperty("CODCONS", consulta.getCodcons());
                    dvoReserva.setProperty("SITUACAO",  "PE");
                    
                     
                    // Salva o registro no banco de dados
                    entityFacade.createEntity("AD_FACILITARES", (EntityVO) dvoReserva);
            
                }
            }
            
            // Marca como processados
            consultaDAO.marcarProcessado(consulta.getCodcons());
    		
    	}
    	
    }    
	
	
    public List<Reservas> carregarAProcessar() throws Exception {
        final List<Reservas> reservas = new ArrayList<>();

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select *" + 
			                "  from ad_facilitares res" + 
			                " where res.situacao = 'PE'" + 
			                " order by res.nures");
        
        System.out.println("Irá carregar reservas pendentes");

        try (final ResultSet resultSet = nativeSql.executeQuery();) {
            while (resultSet.next()) {
                final Reservas reserva = new Reservas();
                
                System.out.println("Nome: " + resultSet.getString("nome_cliente"));
                reserva.setNures(resultSet.getBigDecimal("nures"));
                reserva.setCodnegocio(resultSet.getBigDecimal("codnegocio_facilita"));
                reserva.setNome_negocio(resultSet.getString("nome_negocio"));      
          		reserva.setCodproduto(resultSet.getBigDecimal("codprod_facilita"));  
                reserva.setProduto_empreendimento(resultSet.getString("produto_empreendimento"));                     
                reserva.setCriacao_negocio(resultSet.getDate("criacao_negocio"));                
                reserva.setCpf(resultSet.getString("cpf"));      
                reserva.setCnpj(resultSet.getString("cnpj"));      
                reserva.setCodunidade(resultSet.getBigDecimal("codunidade_facilita"));  
                reserva.setQuadra_bloco(resultSet.getString("quadra_bloco"));           
                reserva.setNumero_unidade(resultSet.getString("numero_unidade"));             
                reserva.setNome_cliente(resultSet.getString("nome_cliente"));           
                reserva.setSexo(resultSet.getString("sexo"));      
                reserva.setTelefone(resultSet.getString("telefone"));       
                reserva.setCelular(resultSet.getString("celular"));      
                reserva.setEmail(resultSet.getString("email"));      
                reserva.setCpf_corretor(resultSet.getString("cpf_corretor"));           
                reserva.setStatus_reserva(resultSet.getString("status_reserva"));             
                reserva.setStatus_negociacao(resultSet.getString("status_negociacao"));                
                reserva.setOrigens(resultSet.getString("origens"));      
                reserva.setAtualizacao_negocio(resultSet.getDate("atualizacao_negocio"));                    
                reserva.setValor_negocio_venda(resultSet.getBigDecimal("valor_negocio_venda"));                  
                reserva.setEtapa_funil(resultSet.getString("etapa_funil"));          
                reserva.setEquipe(resultSet.getString("equipe"));      
                reserva.setNome_negocio_responsavel(resultSet.getString("nome_negocio_responsavel"));                       
                reserva.setNome_gerente_negocio(resultSet.getString("nome_gerente_negocio"));                   
                reserva.setNome_corretor_negocio(resultSet.getString("nome_corretor_negocio"));                    
                reserva.setEmail_responsavel(resultSet.getString("email_responsavel"));                
                reserva.setEmail_gerente(resultSet.getString("email_gerente"));            
                reserva.setEmail_corretor(resultSet.getString("email_corretor"));             
                reserva.setRequisitou_reserva(resultSet.getString("resquisitou_reserva"));      
                reserva.setCriacao_reserva(resultSet.getDate("criacao_reserva"));        
                reserva.setCodcons(resultSet.getBigDecimal("codcons"));  
                reserva.setLogerro(resultSet.getString("logerro"));

                reservas.add(reserva);
            }
        }

        System.out.println("Irá retornar lista com reservas pendentes");
        return reservas;
    }
	

    public List<BigDecimal> carregarACancelar() throws Exception {
        final List<BigDecimal> reservas = new ArrayList<>();

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select res.nures" + 
			        		"  from ad_facilitares  res" + 
			        		" where res.situacao = 'PR'" + 
			        		"   and ((res.codcons  < (select max(codcons) from ad_facilitacons) and" + 
			        		"         not exists (select 1" + 
			        		"                       from ad_facilitanegoc neg" + 
			        		"                      where neg.codprodfacilita    = res.codprod_facilita" + 
			        		"                        and neg.codnegociofacilita = res.codnegocio_facilita" + 
			        		"                        and neg.codunidadefacilita = res.codunidade_facilita" + 
			        		"                        and neg.codcons            > res.codcons" +
			        		"                        and neg.codcons            = (select max(codcons) from martins.ad_facilitacons))))");
        
        System.out.println("Irá carregar reservas a serem canceladas");

        try (final ResultSet resultSet = nativeSql.executeQuery();) {
            while (resultSet.next()) {

                reservas.add(resultSet.getBigDecimal("nures"));
                System.out.println("Reserva: " + resultSet.getBigDecimal("nures"));
            }
        }

        System.out.println("Irá retornar lista com reservas a serem canceladas");
        return reservas;
    }
    
    
    public Integer verificaNegocioExiste(final BigDecimal codProdFacilita, 
    									 final BigDecimal codNegocioFacilita,
    									 final BigDecimal codUnidadeFacilita) {
    	
    	
        Integer qtde=0;
    	
    	final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        try {
			nativeSql.appendSql("select count(1) qtde" + 
					            "  from ad_facilitares" + 
					            " where codprod_facilita=" + codProdFacilita +
					            "   and codnegocio_facilita=" + codNegocioFacilita +
					            "   and codunidade_facilita =" + codUnidadeFacilita);
			
			final ResultSet resultSet = nativeSql.executeQuery();
			if (resultSet.next()) {
				qtde = resultSet.getInt("qtde");
			}
		} catch (Exception e) {
			qtde = 0;
		}

        return qtde;

    }
	
    public void marcarProcessado(final BigDecimal nures, final BigDecimal idFac) throws SQLException {
        final String sql = "" +
                "begin " +
                "  update ad_facilitares" +
                "     set situacao = 'PR'," +
                "         faccodigo = :faccodigo," +
                "         logerro = null" +
                "   where nures = :nures;" +
                "end;";

        final Connection connection = jdbcWrapper.getConnection();
        try (final CallableStatement callableStatement = connection.prepareCall(sql)) {
        	callableStatement.setBigDecimal("faccodigo", idFac);
        	callableStatement.setBigDecimal("nures", nures);

            callableStatement.execute();
        }
    }

    public void marcarCancelado(final BigDecimal nures) throws SQLException {
        final String sql = "" +
                "begin " +
                "  update ad_facilitares" +
                "     set situacao = 'RC'," +
                "         logerro = null" +
                "   where nures = :nures;" +
                "end;";

        final Connection connection = jdbcWrapper.getConnection();
        try (final CallableStatement callableStatement = connection.prepareCall(sql)) {
        	callableStatement.setBigDecimal("nures", nures);

            callableStatement.execute();
        }
    }

    
    public void marcarFalha(final Reservas reserva, final String msgerro) throws SQLException {
    	
    	final DiversosDAO diversosDAO = new DiversosDAO(jdbcWrapper);
    	
        final String sql = "" +
                "begin " +
                "  update ad_facilitares" +
                "    set logerro  = :logerro," +
                "        situacao = 'ER'" +
                "  where nures = :nures;" +
                "end;";

        final Connection connection = jdbcWrapper.getConnection();
        try (final CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setString("logerro", msgerro);
            callableStatement.setBigDecimal("nures", reserva.getNures());
            callableStatement.execute();
            
    		try {
				diversosDAO.enviaAvisos("FACILITA - Problema Reserva", 
						"Houve um problema no processamento de uma ou mais reservas. Verificar na tela INTEGRAÇÃO FACILITA - RESERVAS e filtrar por Situação = ERRO.", 
						BigDecimal.valueOf(3));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    }     
        
    public void mudaStatusImovel(final BigDecimal idImovel,
    							 final String statusImovel) throws SQLException {
        
    	final String sql = "" +
                "begin " +
                "  update timimv" +
                "    set imvestagio = '" + statusImovel + "'" +
                "  where imvcodigo = :codigo;" +
                "end;";

        final Connection connection = jdbcWrapper.getConnection();
        try (final CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setBigDecimal("codigo", idImovel);

            callableStatement.execute();
        }
    }     

    public void mudaStatusLote(final BigDecimal idLote,
    						   final String statusLote) throws SQLException {
        
    	final String sql = "" +
                "begin " +
                "  update timlte" +
                "     set ltesituacao = '" + statusLote + "'" +
                "   where ltecodigo = :codigo;" +
                "end;";

        final Connection connection = jdbcWrapper.getConnection();
        try (final CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setBigDecimal("codigo", idLote);

            callableStatement.execute();
        }
    }       

    
}
