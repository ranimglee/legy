package payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import payment.domain.model.enums.PaymentStatus;
import payment.domain.model.enums.PaymentType;
import payment.domain.model.enums.RecurrenceType;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    private static final String DEFAULT_CURRENCY = "CFA";

    private String id;
    private Double amount;
    private Date date;
    private PaymentStatus status;
    private PaymentType type;
    private String beneficiary;
    private String currency = DEFAULT_CURRENCY;
    private boolean recurring;
    private Date recurrenceEndDate;
    private RecurrenceType recurrenceType;
    private String userId;
    private String beneficiaryRIB;

}