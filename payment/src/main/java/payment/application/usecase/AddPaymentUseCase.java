package payment.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payment.application.dto.In.PaymentRequest;
import payment.domain.model.Payment;
import payment.domain.model.PaymentCalendar;
import payment.domain.model.enums.PaymentStatus;
import payment.domain.service.PaymentDomainService;

import java.util.List;

@Service
@RequiredArgsConstructor

public class AddPaymentUseCase {

    private final PaymentDomainService paymentDomainService;


        public List<PaymentCalendar> execute(PaymentRequest request) {
            Payment payment = new Payment();
            payment.setAmount(request.getAmount());
            payment.setDate(request.getDate());
            payment.setType(request.getType());
            payment.setBeneficiary(request.getBeneficiary());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setUserId(request.getUserId());
            payment.setBeneficiaryRIB(request.getBeneficiaryRIB());

            if (request.isRecurring() && request.getRecurrenceEndDate() != null && request.getRecurrenceType() != null) {
                payment.setRecurring(true);
                payment.setRecurrenceType(request.getRecurrenceType());
                payment.setRecurrenceEndDate(request.getRecurrenceEndDate());
                return paymentDomainService.addRecurringPayments(payment, request.getRecurrenceEndDate(), request.getRecurrenceType());
            } else {
                return List.of(paymentDomainService.addPaymentToDay(payment));
            }
        }
    }

