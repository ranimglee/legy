package shared.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserOrderRecommendationEvent(
        @JsonProperty("user_id") String userId
) {}
