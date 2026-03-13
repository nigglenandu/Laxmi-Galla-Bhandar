package core.security.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Centralized, reusable hashing utilities for core module.
 * Uses SHA-256 + Base64 encoding (URL-safe, no padding).
 */
@Slf4j
@UtilityClass
public class HashUtils {

    private static final String ALGORITHM = "SHA-256";

    /**
     * Compute SHA-256 hash and return Base64-encoded string.
     * Returns null if input is blank or null.
     */
    public String sha256Base64(String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("Hashing failure", e);
        }
    }
}