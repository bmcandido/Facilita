package daos;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import models.Imoveis;
import models.Reservas;

public class FacDAO {

    private final JdbcWrapper jdbcWrapper;

    public FacDAO(final JdbcWrapper jdbcWrapper) {
        this.jdbcWrapper = jdbcWrapper;
    }


    public final BigDecimal gravaFacDB(final Reservas reserva, 
                           			   final Imoveis imovel,
                           			   final BigDecimal idCorretor,
                           			   final BigDecimal idProspect,
                           			   final BigDecimal idParceiro) throws Exception {
    	
        final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        final SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final ChaveDAO chaveDAO = new ChaveDAO(jdbcWrapper);
        final BigDecimal idFAC;
        
        // Cria value object para a instância do jape
        final DynamicVO dvoFAC = (DynamicVO) entityFacade.getDefaultValueObjectInstance("TimFichaAtendimentoCliente");
        
        idFAC=chaveDAO.geraTGFNUM("TIMFAC", "FACCODIGO");
        System.out.println("FACCOGIDO "+ idFAC);
        
        dvoFAC.setProperty("FACCODIGO", idFAC);
        dvoFAC.setProperty("FACESTAGIO", "N");  // N: NEGOCIAÇÃO
        dvoFAC.setProperty("FACBUSCAPOR","CO"); // CO: COMPRAR
        dvoFAC.setProperty("FACCORRETOR", idCorretor);
        dvoFAC.setProperty("FACLANCAMENTO", Timestamp.valueOf(sdfDB.format(new Date())));
        dvoFAC.setProperty("FACUSUARIO", 0);
        dvoFAC.setProperty("FACEMPREENDIMENTO", imovel.getEmpreendimento());
        dvoFAC.setProperty("FACTIPOIMOVEL", imovel.getTipoImovel());
        dvoFAC.setProperty("FACBAIRROIMOVEL", imovel.getBairro());
        dvoFAC.setProperty("FACEDIFICIO", imovel.getEdificio());
        dvoFAC.setProperty("FACPROSPECT", idProspect);
        dvoFAC.setProperty("FACDHALTER", Timestamp.valueOf(sdfDB.format(new Date()))); 
        dvoFAC.setProperty("FACUSUALTER", 0);
        dvoFAC.setProperty("FACCODPARC", idParceiro);
        // Salva o registro no banco de dados
        entityFacade.createEntity("TimFichaAtendimentoCliente", (EntityVO) dvoFAC);
        
        System.out.println("Gerou registro da FAC");
        
        
        // Cria value object para a instância do jape
        final DynamicVO dvoIPR = (DynamicVO) entityFacade.getDefaultValueObjectInstance("TimImovelFAC");
        dvoIPR.setProperty("IPRFAC", idFAC);
        dvoIPR.setProperty("IPRIMOVEL", imovel.getCodigo());
        dvoIPR.setProperty("IPRUSUARIO", BigDecimal.ZERO);
        dvoIPR.setProperty("IPRDATA", Timestamp.valueOf(sdfDB.format(new Date())));
        dvoIPR.setProperty("IPRCHAVE","N");       // N:NÃO
//        dvoIPR.setProperty("IPRPROPESTAGIO","P"); // P:PROPOSTA
        dvoIPR.setProperty("IPRPROPPARA","V");    // V:VENDA
        dvoIPR.setProperty("IPRDHPROP",Timestamp.valueOf(sdfDB.format(new Date())));
        dvoIPR.setProperty("AD_VLRCONTRATO",reserva.getValor_negocio_venda());
        // Salva o registro no banco de dados
        entityFacade.createEntity("TimImovelFAC", (EntityVO) dvoIPR);
        
        System.out.println("Gerou registro IPR");
		        
        return idFAC;
        
    }    

}
