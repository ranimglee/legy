package payment.application.dto.Out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoiChartPoint {
    private String date;
    private double roi;
}