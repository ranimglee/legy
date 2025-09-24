package user.application.dto.out;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record RegisterRestaurantManagerRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @NotBlank(message = "Firstname is required")
        String firstname,

        @NotBlank(message = "Lastname is required")
        String lastname,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,


        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^\\+\\d{1,3}\\d{9,14}$",
                message = "Phone number format is invalid (e.g., +221XXXXXXXXX)"
        )
        String phoneNumber,

        @NotBlank(message = "RIB is required")
        String rib,

        String createdBy,
        Instant createdAt

) {}
