package dev.gladikov.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ верификации токена")
public class JwtVerificationResponse {
    @Schema(description = "email", example = "user@mail.com")
    private String email;

    @Schema(description = "Роли доступные пользователю", example = "user, blocked")
    private String roles;
}