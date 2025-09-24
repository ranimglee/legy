package payment.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payment.application.dto.In.PaymentMethodRequest;
import payment.application.service.PaymentMethodService;
import payment.domain.model.PaymentMethod;
import payment.domain.model.enums.PaymentMethodType;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddPaymentMethodUseCase {
    private final PaymentMethodService paymentMethodService;

    public PaymentMethod execute(PaymentMethodRequest request) {
        request.validatePaymentMethod();

        // Set expiration date (if applicable) and create a new PaymentMethod
        request.setExpirationDate(request.getExpirationDate());
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(request.getType());
        paymentMethod.setProvider(request.getProvider());
        paymentMethod.setHolderName(request.getHolderName());
        paymentMethod.setDefault(request.isDefault());
        paymentMethod.setCreatedAt(LocalDateTime.now());
        paymentMethod.setUserId(request.getUserId());

        // Check if the new payment method is marked as default
        if (paymentMethod.isDefault()) {
            // Reset other payment methods to not default for the same user
            resetOtherDefaultPaymentMethods(paymentMethod.getUserId());
        }

        // Set additional fields based on payment method type
        if (request.getType() == PaymentMethodType.CREDIT_CARD) {
            paymentMethod.setExpirationDate(request.getExpirationDate());
            paymentMethod.setCvc(request.getCvc());
        } else if (request.getType() == PaymentMethodType.PAYPAL) {
            paymentMethod.setPaypalEmail(request.getPaypalEmail());
        } else if (request.getType() == PaymentMethodType.BANK_TRANSFER) {
            paymentMethod.setAccountNumber(request.getAccountNumber());
        }

        // Call the service to create the payment method
        return paymentMethodService.createPaymentMethod(paymentMethod);
    }

    private void resetOtherDefaultPaymentMethods(String userId) {
        // Fetch all the payment methods for the given user
        List<PaymentMethod> userPaymentMethods = paymentMethodService.getPaymentMethodsByUserId(userId);

        // Update all other payment methods to not default
        for (PaymentMethod existingPaymentMethod : userPaymentMethods) {
            if (existingPaymentMethod.isDefault()) {
                existingPaymentMethod.setDefault(false);
                paymentMethodService.updatePaymentMethod(existingPaymentMethod.getId(), existingPaymentMethod);
            }
        }
    }
}
