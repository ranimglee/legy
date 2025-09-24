package payment.application.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payment.application.dto.In.PaymentMethodRequest;
import payment.domain.model.PaymentMethod;
import payment.domain.repository.PaymentMethodRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;



    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod.getUserId() == null || paymentMethod.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID must be set on the payment method before saving.");
        }
        return paymentMethodRepository.save(paymentMethod);
    }


    public Optional<PaymentMethod> getPaymentMethodById(String id) {
        return paymentMethodRepository.findById(id);
    }


    public Optional<PaymentMethod> updatePaymentMethod(String id, PaymentMethodRequest paymentMethodRequest) {
        Optional<PaymentMethod> existingPaymentMethod = paymentMethodRepository.findById(id);
        if (existingPaymentMethod.isPresent()) {
            PaymentMethod paymentMethod = existingPaymentMethod.get();

            // Assuming paymentMethodRequest has setter methods to update properties of the paymentMethod entity
            paymentMethod.setAccountNumber(paymentMethodRequest.getAccountNumber());
            paymentMethod.setType(paymentMethodRequest.getType());
            paymentMethod.setHolderName(paymentMethodRequest.getHolderName());
            paymentMethod.setProvider(paymentMethodRequest.getProvider());
            paymentMethod.setDefault(paymentMethodRequest.isDefault());
            paymentMethod.setUpdatedAt(LocalDateTime.now());


            return Optional.of(paymentMethodRepository.save(paymentMethod));
        }
        return Optional.empty();
    }


    // Delete a payment method
    public boolean deletePaymentMethod(String id) {
        if (paymentMethodRepository.existsById(id)) {
            paymentMethodRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public Optional<PaymentMethod> findById(String id) {

        return paymentMethodRepository.findById(id);
    }
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public List<PaymentMethod> getPaymentMethodsByUserId(String userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    public PaymentMethod updatePaymentMethod(String id, PaymentMethod updatedMethod) {
        Optional<PaymentMethod> existingMethod = paymentMethodRepository.findById(id);
        if (existingMethod.isPresent()) {
            PaymentMethod method = existingMethod.get();
            method.setDefault(updatedMethod.isDefault());
            return paymentMethodRepository.save(method);
        }
        return null;
    }


}