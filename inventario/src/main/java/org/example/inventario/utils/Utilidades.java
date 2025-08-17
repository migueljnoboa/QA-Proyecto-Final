package org.example.inventario.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utilidades {
    public static String convertToJson(Object objeto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(objeto);
    }

    public static String convertAndFormatBigDecimalToString(Object objeto) {
        if (objeto == null) {
            return "0.00";
        }
        if (objeto instanceof Number) {
            return String.format("%,.2f", ((Number) objeto).doubleValue());
        }
        return objeto.toString();
    }
}
