package org.example.inventario.model.dto.scurity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response of user authentication in the inventory system.")
public record ReturnAuthentication(
        @Schema(description = "Token generated for the authenticated user", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
){
}
