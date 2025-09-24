package ordering.infrastructure.Document;


import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data

public class ReviewDocument {

    @Id
    private String id;

    private String User;
    private String Rating;
    private String Review;
    private String Restaurant;
    private LocalDate Date;
    private String Platform;   // e.g., "Google Maps"

}