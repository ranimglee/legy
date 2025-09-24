package ordering.application.dto.payout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourierPayoutResponseDto {
    // From CourierProfileDTO
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;

    // From WeeklyCourierPaymentSummary
    private int totalDeliveries;
    private int totalRefusals;
    private double totalDeliveryPayout;
    private double bonus;
    private double penalty;
    private double finalPayout;
    private int totalOrders;
    private double avgPayoutPerDelivery;
}