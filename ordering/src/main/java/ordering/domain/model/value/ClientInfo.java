package ordering.domain.model.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientInfo {

    private String clientId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Double longitude;
    private Double latitude;


}
