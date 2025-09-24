package user.application.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shared.dto.ClientProfileDTO;
import shared.domain.service.ClientQueryService;
import user.domain.repository.UserRepository;
import user.infrastructure.mapper.ClientMapper;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientQueryServiceImpl implements ClientQueryService {

    private final UserRepository userRepository;

    @Override
    public ClientProfileDTO getClientInfoById(String userId) {
        return userRepository.findClientById(userId)
                .map(clientEntity -> new ClientProfileDTO(
                        clientEntity.getId(),
                        clientEntity.getFirstname(),
                        clientEntity.getLastname(),
                        clientEntity.getPhoneNumber(),
                        clientEntity.getAddress(),
                        clientEntity.getLongitude(),
                        clientEntity.getLatitude()
                       // clientEntity.getLatitude(),
                       // clientEntity.getLongitude()
                ))
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }
    @Override
    public List<ClientProfileDTO> getClientsByIds(List<String> userIds) {
        return userRepository.findByIdIn(userIds)
                .stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
    }
}
