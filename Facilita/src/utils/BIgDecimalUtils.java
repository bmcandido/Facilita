package utils;

import java.math.BigDecimal;

public class BIgDecimalUtils {
    public static BigDecimal get(final String valor) {
        return valor != null ? new BigDecimal(valor) : null;
    }
}
