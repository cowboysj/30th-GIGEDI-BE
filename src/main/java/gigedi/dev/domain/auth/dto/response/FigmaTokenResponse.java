package gigedi.dev.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FigmaTokenResponse(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") Long expiresIn) {}
