package recommendation.domain.event;

import java.io.Serializable;

public record SearchEvent(String userId, String keyword) implements Serializable {
}
