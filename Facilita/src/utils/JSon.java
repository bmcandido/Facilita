package utils;

import java.math.BigDecimal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JSon {

    public static JsonObject getAsJsonObjectFromJsonObject(final JsonObject jsonObject, final String attribute) {
        final JsonElement jsonElement = jsonObject.get(attribute);
        if (!jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else {
            return null;
        }
    }

    public static JsonArray getAsJsonArrayFromJsonObject(final JsonObject jsonObject, final String attribute) {
        final JsonElement jsonElement = jsonObject.get(attribute);
        if (!jsonElement.isJsonNull() && jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        } else {
            return null;
        }
    }

    public static String restringeTamanho(final String valor, final int tamanho) {
        return valor.substring(0, valor.length() > tamanho ? tamanho : valor.length());
    }

    public static String getAsStringFromJsonArray(final JsonArray jArray, final int indice) {
        if (jArray != null) {
            final JsonElement jElement = jArray.get(indice);
            if (jElement != null && !jElement.isJsonNull()) {
                return restringeTamanho(jElement.getAsString(), 100);
            }
        }
        return null;
    }

    public static BigDecimal getAsBigDecimalFromJsonArray(final JsonArray jArray, final int indice) {
        if (jArray != null) {
            final JsonElement jElement = jArray.get(indice);
            if (jElement != null && !jElement.isJsonNull()) {
                return jElement.getAsBigDecimal();
            }
        }
        return new BigDecimal(0);
    }

    public static String getAsStringFromJsonObject(final JsonObject jObject, final String attribute) {
        if (jObject != null) {
            final JsonElement jElement = jObject.get(attribute);
            if (jElement != null && !jElement.isJsonNull()) {
                return restringeTamanho(jElement.getAsString(), 100);
            }
        }
        return null;
    }

    public static BigDecimal getAsBigDecimalFromJsonObject(final JsonObject jObject, final String attribute) {
        if (jObject != null) {
            final JsonElement jElement = jObject.get(attribute);
            if (jElement != null && !jElement.isJsonNull()) {
                if (jElement.getAsJsonPrimitive().isNumber()) {
                    return jElement.getAsBigDecimal();
                } else if (!jElement.getAsString().isEmpty()) {
                    return new BigDecimal(jElement.getAsString());
                }
            }
        }
        return new BigDecimal(0);
    }
    
    public static String ajustaJsonFacilita(String conteudo) {
    	
    	conteudo = conteudo.trim();
//    	Integer tamConteudo = conteudo.length();
    	
    	System.out.println("Entrou ajustaJsonFacilita");
    	
//    	System.out.println("conteudo.substring(0, 1): "+conteudo.substring(0, 1));
//    	System.out.println("conteudo.substring(tamConteudo-1, tamConteudo): "+conteudo.substring(tamConteudo-1, tamConteudo));
    	
    	
//    	if (conteudo.substring(0, 1) == "[" && conteudo.substring(tamConteudo-1, tamConteudo) == "]") {
//    		System.out.println("Entrou if");
//    		conteudo = conteudo.substring(1, tamConteudo-1);
    	    conteudo = "{" + 
    	    		"  \"success\": 1,\n" + 
    	    		"  \"error\": [],\n" + 
    	    		"  \"data\": " + conteudo + "}"; 
    		System.out.println("Novo conteudo: "+conteudo);
//    	}
    	
    	return conteudo;
    	
    }
}