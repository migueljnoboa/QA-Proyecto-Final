package org.example.inventario.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MyException.class})
    protected ResponseEntity<Object> manejarMiExcepcion(RuntimeException ex, WebRequest request) {
        MyException exception = (MyException) ex;

        return handleExceptionInternal(
                ex,
                ResponseMessage.errorMyExcepcion(exception),
                new HttpHeaders(),
                exception.getHttpStatus(),
                request
        );
    }
}
