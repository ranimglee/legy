package payment.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payment.domain.model.Payment;
import payment.domain.model.PaymentMethod;
import payment.domain.service.NotificationService;
import payment.domain.service.PaymentDomainService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class GetPaymentsUseCase {

    private final PaymentDomainService paymentDomainService;


    /**
     * Retrieves all payments for a specific date, filtered by type and/or status if provided.
     *
     * @param date   The date to filter payments.
     * @param type   Optional filter for payment type.
     * @param status Optional filter for payment status.
     * @return A list of filtered payments.
     */
    public List<Payment> execute(Date date, String type, String status) {
        return paymentDomainService.getFilteredPayments(date, type, status);
    }
    /**
     * Retrieves all payments.
     *
     * @return A list of all payments.
     */
    public List<Payment> getAllPayments() {
        return paymentDomainService.getAllPayments();
    }


    /**
     * Modify payment details (amount, date, payment method, beneficiary).
     *
     * @param paymentId The ID of the payment to modify.
     * @param amount     The new amount of the payment.
     * @param newDate    The new date of the payment.
     * @param beneficiary The new payment beneficiary.
     * @return The modified payment.
     */
    public Payment modifyPayment(String paymentId, Double amount, Date newDate, String beneficiary) {
        return paymentDomainService.modifyPayment(paymentId, amount, newDate, beneficiary);
    }

    /**
     * Postpones a payment to a new date.
     * @param paymentId The payment ID to postpone.
     * @param newDate The new date to postpone the payment to.
     * @return The modified payment.
     */
    public Payment postponePayment(String paymentId, Date newDate) {
        return paymentDomainService.postponePayment(paymentId, newDate);
    }


    public List<Payment> getProcessingPayments() {
        return paymentDomainService.getProcessingPayments();
    }
}
