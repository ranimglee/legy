package payment.infrastructure.persistence.paymentMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import payment.domain.model.PaymentMethod;
import payment.domain.model.enums.PaymentMethodType;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payment_methods")
public class PaymentMethodDocument {
    @Id
    private String id;
    private PaymentMethodType type;
    private String provider;
    private String accountNumber;
    private String holderName;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate expirationDate;
    private String cvc;
    private String paypalEmail;
    private String userId;

  //  private String bankRoutingNumber;
    public PaymentMethod toDomain() {
        return new PaymentMethod(
                id,
                type,
                provider,
                accountNumber,
                holderName,
                isDefault,
                createdAt,
                updatedAt,
                expirationDate,
                cvc,
                paypalEmail,
                //bankRoutingNumber,
                userId
        );
    }

    public static PaymentMethodDocument fromDomain(PaymentMethod method) {
        return new PaymentMethodDocument(
                method.getId(),
                method.getType(),
                method.getProvider(),
                method.getAccountNumber(),
                method.getHolderName(),
                method.isDefault(),
                method.getCreatedAt() ,
                method.getUpdatedAt(),
                method.getExpirationDate(),
                method.getCvc(),
                method.getPaypalEmail(),
             //   method.getBankRoutingNumber(),
                method.getUserId()
        );
    }
}
