package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Set;

//@PasswordMatch// ← custom validator for vendorType + role consistency
@Schema(description = "Request object for user signup/registration")
public record SignupRequest(

    @NotBlank(message = "{firstName.required}")
    @Size(min = 2, max = 80, message = "{firstName.size}")
//    @ValidFirstName
    @Schema(description = "User's first name", example = "John", required = true)
    String firstName,

    @Size(max = 80, message = "{lastName.size}")
//    @ValidLastName
    @Schema(description = "User's last name (optional)", example = "Doe")
    String lastName,

    @NotBlank(message = "{phoneNumber.required}")
//    @ValidNepaliPhone
//    @UniquePhone
    @Schema(description = "Nepali phone number (+977 format)", example = "+9779800000000", required = true)
    String phoneNumber,

    @NotBlank(message = "{email.required}")
    @Email(message = "{email.format.invalid}")
    @Size(max = 255, message = "{email.size}")
//    @UniqueEmail
    @Schema(description = "Email address", example = "nandu@example.com", required = true)
    String email,

    @NotBlank(message = "{password.required}")
    @Size(min = 8, max = 255, message = "{password.size}")
//    @ValidPassword
    @Schema(description = "Password (must meet complexity rules)", example = "Password@123", required = true)
    String password,

    @NotBlank(message = "{password.required}")
    @Size(min = 8, max = 255, message = "{password.size}")
//    @ValidPassword
    @Schema(description = "Confirm password (must match password)", example = "Password@123", required = true)
    String confirmPassword,

    @AssertTrue(message = "You must accept terms and conditions")
    @Schema(description = "Confirmation that user accepts terms & privacy policy", required = true)
    boolean termsAccepted,

    @Schema(description = "User roles (e.g., CUSTOMER, VENDOR)", example = "[\"CUSTOMER\"]")
    Set<String> roles
) {}