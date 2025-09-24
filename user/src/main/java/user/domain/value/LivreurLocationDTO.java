package user.domain.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LivreurLocationDTO {
    private String id;
    private String latitude;
    private String longitude;
    private String orderId;

    // Constructor without orderId
    public LivreurLocationDTO(String id, String latitude, String longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
