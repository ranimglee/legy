package shared.domain.service;


import shared.dto.ClientProfileDTO;

import java.util.Arrays;
import java.util.List;

public interface ClientQueryService {
    ClientProfileDTO getClientInfoById(String userId);

    List<ClientProfileDTO> getClientsByIds(List<String> userIds);

}
