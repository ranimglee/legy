package payment.application.dto.In;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import payment.domain.model.enums.InvestmentType;

import java.lang.Double;
import java.util.Map;

@Getter
@Setter
public class InvestmentRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Investor name cannot be empty")
    private String investorName;

    @Size(max = 255, message = "Description can be at most 255 characters")
    private String description;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;



    private String typeId;
    private Map<String, Object> fields;
}