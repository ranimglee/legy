package payment.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import payment.domain.model.enums.PaymentMethodType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"accountNumber", "cvc"})
public class PaymentMethod {

    private String id;
    private PaymentMethodType type;

    private String provider;
    @JsonIgnore
    private String accountNumber;

    private String holderName;

    private boolean isDefault;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDate expirationDate;

    @JsonIgnore
    private String cvc;

    private String paypalEmail;

   // private String bankRoutingNumber;
    private String userId;



}
