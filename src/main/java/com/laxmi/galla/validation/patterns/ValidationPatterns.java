package com.laxmi.galla.validation.patterns;

import java.util.regex.Pattern;

public final class ValidationPatterns {

    // First Name: Strict (capitalized)
    public static final Pattern FIRST_NAME = Pattern.compile(
            "^[A-Z][a-zÀ-ÿ]*(?:[\\s'-][a-zÀ-ÿ]*)*$",
            Pattern.UNICODE_CHARACTER_CLASS
    );

    public static final Pattern LAST_NAME = Pattern.compile(
            "^[a-zA-ZÀ-ÿ]+(?:[\\s'-][a-zA-ZÀ-ÿ]+)*$",
            Pattern.UNICODE_CHARACTER_CLASS
    );

    public static final Pattern PASSWORD = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$"
    );

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 255;



    private ValidationPatterns() {
        throw new UnsupportedOperationException("Utility class");
    }
}