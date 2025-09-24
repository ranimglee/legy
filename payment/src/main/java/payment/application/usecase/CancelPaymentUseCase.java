package payment.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payment.domain.model.Payment;
import payment.domain.service.PaymentDomainService;
@Service
@RequiredArgsConstructor

public class CancelPaymentUseCase {


    private final PaymentDomainService paymentDomainService;


    /**
     * Cancels a payment by changing its status to "canceled".
     *
     * @param paymentId The ID of the payment to cancel.
     * @return The updated payment.
     * @throws RuntimeException if the payment is not found.
     */
    public Payment cancelPayment(String paymentId) {
        return paymentDomainService.cancelPayment(paymentId);
    }
}
