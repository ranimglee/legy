package payment.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import payment.domain.model.enums.InvestmentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    private String id;
    private String ref;
    private String investorName;
    private Double amount;
    private String description;
    private LocalDateTime date;
    private String email;

    private String typeId;
    private Map<String, Object> fieldValues;
    private Map<String, Object> derivedFields;

    private Double revenueGeneratedFromInvestment;


    private Double preMoneyValuation;
    private Double postMoneyValuation;
    private Double ownershipPercentage;
    private Double sharePriceAtInvestment;

    private Double ROI;

}
