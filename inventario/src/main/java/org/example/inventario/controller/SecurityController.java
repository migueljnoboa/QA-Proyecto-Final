package org.example.inventario.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.service.security.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityController {
    private final AuthenticationService authenticationService;
    @Operation(
            summary = "Authenticates a user in the system.",
            description = "This endpoint allows a user to authenticate by providing their username and password. " +
                    "If the credentials are valid, a token will be returned for further requests."
    )
    @PostMapping("/authenticate")
    public ReturnAuthentication authenticate(@RequestBody UserAuthentication userAuthentication) {
        return authenticationService.authenticate(userAuthentication);
    }
}
