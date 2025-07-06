package org.example.inventario.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@ToString
public class MyException extends RuntimeException {

    public static final int ERROR_VALIDATION = 4000;
    public static final int ERROR_AUTHENTICATION = 4001;
    public static final int ERROR_JWT_NOT_FOUND = 4002;
    public static final int ERROR_JWT_VERIFICATION = 4003;
    public static final int ERROR_JWT_EXPIRED = 4004;
    public static final int ERROR_USER_NOT_FOUND = 4005;
    public static final int ERROR_USER_TOKEN_DISABLED = 4006;
    public static final int ERROR_USER_NO_PERMISSIONS = 4009;

    public static final int ERROR_NOT_FOUND = 4040;


    public static final int ERROR_SERVER = 5000;

    int code;
    String message;

    public MyException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return switch (code) {
            case MyException.ERROR_VALIDATION,
                 MyException.ERROR_JWT_NOT_FOUND,
                 MyException.ERROR_AUTHENTICATION,
                 MyException.ERROR_USER_NOT_FOUND -> HttpStatus.UNAUTHORIZED;

            case MyException.ERROR_JWT_VERIFICATION,
                 MyException.ERROR_JWT_EXPIRED,
                 MyException.ERROR_USER_NO_PERMISSIONS,
                 MyException.ERROR_USER_TOKEN_DISABLED -> HttpStatus.FORBIDDEN;
            case MyException.ERROR_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case MyException.ERROR_SERVER -> HttpStatus.INTERNAL_SERVER_ERROR;

            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
