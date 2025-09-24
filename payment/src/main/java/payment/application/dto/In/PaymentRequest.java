package payment.application.dto.In;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import payment.domain.model.PaymentMethod;
import payment.domain.model.enums.PaymentMethodType;
import payment.domain.model.enums.PaymentStatus;
import payment.domain.model.enums.PaymentType;
import payment.domain.model.enums.RecurrenceType;

import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
public class PaymentRequest {
    @Valid
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be greater than or equal to 0")
    private Double amount;

   @NotNull(message = "Date is required")
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")

    private Date date;


    @NotNull(message = "Payment type is required")
    private PaymentType type;

    @NotBlank(message = "Beneficiary is required")
    private String beneficiary;

    private boolean recurring;
    private Date recurrenceEndDate;
    private RecurrenceType recurrenceType;
    private String userId;
    private String beneficiaryRIB;


}
