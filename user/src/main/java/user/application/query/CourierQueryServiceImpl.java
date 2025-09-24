package user.application.query;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shared.domain.service.CourierQueryService;
import shared.dto.CourierProfileDTO;
import user.domain.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourierQueryServiceImpl implements CourierQueryService {
    private static final Logger logger = LoggerFactory.getLogger(CourierQueryServiceImpl.class);

    private final UserRepository courierRepository;

    @Override
    public List<CourierProfileDTO> getAllCouriers() {
        var couriers = courierRepository.findAllLivreurs().stream()

                .map(courier -> new CourierProfileDTO(
                        courier.getId(),
                        courier.getFirstname(),
                        courier.getLastname(),
                        courier.getPhoneNumber(),
                        courier.getMatricule(),
                        courier.getRib(),
                        courier.getEmail()
                ))
                .collect(Collectors.toList());

        logger.info("Fetched {} couriers for weekly payout: {}", couriers.size(), couriers);

        return couriers;
    }



}