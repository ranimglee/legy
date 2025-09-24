package restaurant.application.mapper;

import restaurant.application.dto.Restaurant.OpeningHourDTO;
import restaurant.application.dto.Restaurant.OpeningIntervalDTO;
import restaurant.domain.model.OpeningHour;
import restaurant.domain.model.OpeningInterval;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalTime;

public class OpeningHourMapper {

    public static OpeningInterval toDomain(OpeningIntervalDTO dto) {
        return OpeningInterval.builder()
                .start(LocalTime.parse(dto.getStart()))
                .end(LocalTime.parse(dto.getEnd()))
                .build();
    }

    public static OpeningIntervalDTO toDTO(OpeningInterval interval) {
        return new OpeningIntervalDTO(
                interval.getStart().toString(),
                interval.getEnd().toString()
        );
    }

    public static List<OpeningInterval> toDomainIntervals(List<OpeningIntervalDTO> dtos) {
        return dtos.stream()
                .map(OpeningHourMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static List<OpeningIntervalDTO> toDTOIntervals(List<OpeningInterval> intervals) {
        return intervals.stream()
                .map(OpeningHourMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static OpeningHour toDomain(OpeningHourDTO dto) {
        return OpeningHour.builder()
                .day(DayOfWeek.valueOf(dto.getDay().toUpperCase()))
                .intervals(toDomainIntervals(dto.getIntervals()))
                .closed(dto.isClosed())
                .build();
    }

    public static OpeningHourDTO toDTO(OpeningHour domain) {
        OpeningHourDTO dto = new OpeningHourDTO();
        dto.setDay(domain.getDay().name());
        dto.setIntervals(toDTOIntervals(domain.getIntervals()));
        dto.setClosed(domain.isClosed());
        return dto;
    }

    public static List<OpeningHour> toDomainList(List<OpeningHourDTO> dtos) {
        return dtos.stream().map(OpeningHourMapper::toDomain).collect(Collectors.toList());
    }

    public static List<OpeningHourDTO> toDTOList(List<OpeningHour> domains) {
        return domains.stream().map(OpeningHourMapper::toDTO).collect(Collectors.toList());
    }
}
