package payment.application.dto.In;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestPayoutRequest {
   private Double amount;
   private String phoneNumber;
   private String walletType;
}
