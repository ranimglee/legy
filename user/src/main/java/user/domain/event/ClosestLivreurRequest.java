package user.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record ClosestLivreurRequest(

         Double latitude,
         Double longitude,
        String correlationId
) implements Serializable {}