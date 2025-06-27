package org.example.inventario.exception;

public record ResponseMessage(int code, String message, boolean error){
    public static ResponseMessage errorMyExcepcion(MyException excepcion) {
        return new ResponseMessage(excepcion.getCode(), excepcion.getMessage(), true);
    }
}
