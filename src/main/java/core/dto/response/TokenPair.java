package core.dto.response;

public record TokenPair(
        String accessToken,
        String refreshToken
) {}