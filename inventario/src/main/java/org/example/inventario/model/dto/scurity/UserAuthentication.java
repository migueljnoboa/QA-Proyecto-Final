package org.example.inventario.model.dto.scurity;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Required body for user authentication in the inventory system.")
public record UserAuthentication(
        @Schema(description = "Username used to authenticate the user to the system", example = "test@gmail.com")
        String username,

        @Schema(description = "Password used to authenticate the user to the system", example = "xxxxxxxxxx")
        String password
) {
}
