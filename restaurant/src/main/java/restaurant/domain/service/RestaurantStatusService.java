package restaurant.domain.service;

import org.springframework.stereotype.Service;
import restaurant.domain.model.*;

import java.time.*;

@Service
public class RestaurantStatusService {
    public boolean isOpenNow(Restaurant restaurant) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek today = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        return restaurant.getHoraires().stream()
                .filter(h -> h.getDay() == today && !h.isClosed())
                .flatMap(h -> h.getIntervals().stream())
                .anyMatch(i -> !currentTime.isBefore(i.getStart()) && !currentTime.isAfter(i.getEnd()));
    }
}
