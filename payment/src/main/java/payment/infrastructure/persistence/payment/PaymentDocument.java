package payment.infrastructure.persistence.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import payment.domain.model.Payment;
import payment.domain.model.PaymentMethod;
import payment.domain.model.enums.PaymentMethodType;
import payment.domain.model.enums.PaymentStatus;
import payment.domain.model.enums.PaymentType;
import payment.domain.model.enums.RecurrenceType;

import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class PaymentDocument {
        @Id
        private String id;
        private Double amount;
        private Date date;
        private PaymentStatus status;
        private PaymentType type;
        private String beneficiary;
        private String currency = "EUR";
        private boolean recurring;
        private Date recurrenceEndDate;
        private RecurrenceType recurrenceType;
        private String userId;
        private String beneficiaryRIB;




        public static PaymentDocument fromDomain(Payment payment) {
                return new PaymentDocument(
                        payment.getId(),
                        payment.getAmount(),
                        payment.getDate(),
                        payment.getStatus(),
                        payment.getType(),
                        payment.getBeneficiary(),
                        payment.getCurrency(),
                        payment.isRecurring(),
                        payment.getRecurrenceEndDate(),
                        payment.getRecurrenceType(),
                        payment.getUserId(),
                        payment.getBeneficiaryRIB()
                );
        }

        public Payment toDomain() {
                return new Payment(
                        id,
                        amount,
                        date,
                        status,
                        type,
                        beneficiary,
                        currency,
                        recurring,
                        recurrenceEndDate,
                        recurrenceType,
                        userId,
                        beneficiaryRIB
                );
        }
    }
