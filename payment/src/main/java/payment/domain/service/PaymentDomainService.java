package payment.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import payment.domain.exception.PaymentNotFoundException;
import payment.domain.exception.InvalidPaymentDataException;
import payment.domain.model.*;
import payment.domain.model.enums.PaymentStatus;
import payment.domain.model.enums.RecurrenceType;
import payment.domain.repository.PaymentCalendarRepository;
import payment.domain.repository.PaymentMethodRepository;
import payment.domain.repository.PaymentRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentDomainService {

    private final PaymentCalendarRepository paymentCalendarRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;



    /**
     * Adds a payment to a specific date in the calendar.
     */
    public PaymentCalendar addPaymentToDay(Payment payment) {
        Optional<PaymentCalendar> optionalCalendar = paymentCalendarRepository.findByDueDate(payment.getDate());

        PaymentCalendar paymentCalendar;
        if (optionalCalendar.isPresent()) {
            paymentCalendar = optionalCalendar.get();
        } else {
            paymentCalendar = new PaymentCalendar();
            paymentCalendar.setDueDate(payment.getDate());
            paymentCalendar.setPayments(new ArrayList<>());
        }

        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new InvalidPaymentDataException("Payment amount must be greater than zero.");
        }



        // Handle recurrence
        if (payment.isRecurring() && payment.getRecurrenceEndDate() != null && payment.getRecurrenceType() != null) {
            payment.setRecurring(true);
        }

        // Save and attach
        payment = paymentRepository.save(payment);
        paymentCalendar.getPayments().add(payment);
        return paymentCalendarRepository.save(paymentCalendar);
    }

    /**
     * Adds a recurring payment based on the recurrence type.
     */
    public List<PaymentCalendar> addRecurringPayments(Payment payment, Date endDate, RecurrenceType recurrenceType) {
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new InvalidPaymentDataException("Payment amount must be greater than zero.");
        }

        // Convert java.util.Date to java.time.LocalDate
        LocalDate paymentDate = payment.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        if (endLocalDate.isBefore(paymentDate)) {
            throw new IllegalArgumentException("endDate doit être après la date du paiement.");
        }

        List<PaymentCalendar> calendars = new ArrayList<>();
        LocalDate currentDate = paymentDate;
        String userId = payment.getUserId();

        if (userId == null) {
            throw new InvalidPaymentDataException("User ID cannot be null for recurring payments.");
        }



        while (!currentDate.isAfter(endLocalDate)) {
            Payment recurringPayment = new Payment();
            recurringPayment.setAmount(payment.getAmount());

            // Convert LocalDate back to java.util.Date
            recurringPayment.setDate(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            recurringPayment.setStatus(payment.getStatus());
            recurringPayment.setType(payment.getType());
            recurringPayment.setBeneficiary(payment.getBeneficiary());
            recurringPayment.setUserId(userId);
            recurringPayment.setRecurring(true);
            recurringPayment.setRecurrenceType(recurrenceType);
            recurringPayment.setRecurrenceEndDate(endDate);

            calendars.add(addPaymentToDay(recurringPayment));

            currentDate = switch (recurrenceType) {
                case DAILY -> currentDate.plusDays(1);
                case WEEKLY -> currentDate.plusWeeks(1);
                case MONTHLY -> currentDate.plusMonths(1);
                case YEARLY -> currentDate.plusYears(1);
            };
        }
        return calendars;
    }








    public List<Payment> getFilteredPayments(Date date, String type, String status) {
        log.info("Fetching payments for Date={}, Type={}, Status={}", date, type, status);

        List<Payment> payments = getPaymentsByDate(date);
        log.info("Total payments found for date {}: {}", date, payments.size());

        if (type != null) {
            payments = payments.stream()
                    .filter(payment -> type.equalsIgnoreCase(payment.getType().name()))
                    .collect(Collectors.toList());
            log.info("Payments after filtering by type '{}': {}", type, payments.size());
        }

        if (status != null) {
            payments = payments.stream()
                    .filter(payment -> status.equalsIgnoreCase(payment.getStatus().name()))
                    .collect(Collectors.toList());
            log.info("Payments after filtering by status '{}': {}", status, payments.size());
        }
                payments = payments.stream()
                .filter(payment -> isSameDay(date, payment.getDate()))
                .collect(Collectors.toList());


        log.info("Final payments count: {}", payments.size());
        return payments;
    }
    // Helper method to compare only the date parts (using LocalDate)
    private boolean isSameDay(Date d1, Date d2) {
        LocalDate localDate1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate1.isEqual(localDate2);
    }




    /**
     * Retrieves all payments for a specific date.
     */
    public List<Payment> getPaymentsByDate(Date date) {
        return paymentRepository.findByDate(date);
    }


        /**
         * Retrieves a payment by its ID.
         */
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
    }

    /**
     * Retrieves all payments.
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Modify payment details (amount, date, payment method, beneficiary).
     */
    public Payment modifyPayment(String paymentId, Double amount, Date newDate, PaymentMethod method, String beneficiary) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (amount != null && amount <= 0) {
            throw new InvalidPaymentDataException("Payment amount must be greater than zero.");
        }

        if (newDate != null) payment.setDate(newDate);
        if (beneficiary != null) payment.setBeneficiary(beneficiary);

        payment.setAmount(amount);  // if valid amount is provided
        payment = paymentRepository.save(payment);
        updatePaymentCalendar(payment);

        return payment;
    }

    /**
     * Postpone a payment to a new date.
     */
    public Payment postponePayment(String paymentId, Date newDate) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        payment.setDate(newDate);
        payment = paymentRepository.save(payment);
        updatePaymentCalendar(payment);

        return payment;
    }

    /**
     * Updates the PaymentCalendar after modification or postponement.
     */
    private void updatePaymentCalendar(Payment payment) {
        paymentCalendarRepository.findByDueDate(payment.getDate())
                .ifPresent(calendar -> {
                    calendar.getPayments().removeIf(p -> p.getId().equals(payment.getId()));
                    paymentCalendarRepository.save(calendar);
                });

        addPaymentToDay(payment);
    }

    /**
     * Cancel a payment by its ID.
     */
    public Payment cancelPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(payment -> {
                    payment.setStatus(PaymentStatus.CANCELED);  // Update status
                    return paymentRepository.save(payment);
                })
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
    }

    public List<Payment> getProcessingPayments() {
        return paymentRepository.findByStatus(PaymentStatus.PROCESSING);
    }



}
