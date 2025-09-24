package shared.domain.service;

import shared.dto.CourierProfileDTO;

import java.util.List;

public interface CourierQueryService {
    List<CourierProfileDTO> getAllCouriers();

}
