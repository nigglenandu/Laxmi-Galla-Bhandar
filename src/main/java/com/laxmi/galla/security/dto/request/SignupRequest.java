package com.laxmi.galla.security.dto.request;

import com.laxmi.galla.validation.annotations.ValidFirstName;
import com.laxmi.galla.validation.annotations.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Set;

@Schema(description = "Request object for user signup/registration")
public record SignupRequest(

        @NotBlank(message = "{firstName.required}")
        @Size(min = 2, max = 80, message = "{firstName.size}")
        @ValidFirstName
        @Schema(
                description = "User's first name",
                example = "Nandu",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String firstName,


        @Size(max = 80, message = "{lastName.size}")
        @Schema(
                description = "User's last name",
                example = "Tharu"
        )
        String lastName,


        @NotBlank(message = "{phoneNumber.required}")
        @Schema(
                description = "User phone number",
                example = "+9779800000000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String phoneNumber,


        @NotBlank(message = "{email.required}")
        @Email(message = "{email.format.invalid}")
        @Size(max = 255, message = "{email.size}")
        @Schema(
                description = "Registered email address",
                example = "nigglenandu@gmail.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email,


        @NotBlank(message = "{password.required}")
        @Size(min = 8, max = 255, message = "{password.size}")
        @ValidPassword
        @Schema(
                description = "Password with required complexity",
                example = "Password@123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String password,


        @NotBlank(message = "{password.required}")
        @Size(min = 8, max = 255, message = "{password.size}")
        @Schema(
                description = "Confirm password must match password",
                example = "Password@123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String confirmPassword,


        @AssertTrue(message = "You must accept terms and conditions")
        @Schema(
                description = "User accepts terms and privacy policy",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        boolean termsAccepted,


        @Schema(
                description = "Assigned system roles",
                example = "[\"STAFF\"]"
        )
        Set<String> roles

) {}