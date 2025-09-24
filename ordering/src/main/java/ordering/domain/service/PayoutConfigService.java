package ordering.domain.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import ordering.application.dto.payout.PayoutConfigEntityResponseDto;
import ordering.application.dto.payout.PayoutConfigRequest;
import ordering.domain.model.PayoutConfigEntity;
import ordering.domain.repository.PayoutConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayoutConfigService {

    private final PayoutConfigRepository repository;
    private static final String DEFAULT = "default";
    private static final String DEFAULT_CONFIG_NOT_FOUND = "Default payout configuration not found";


    // Initialize service with default payout configuration if no configuration exists
    @PostConstruct
    public void init() {
        // Check if there are any existing payout configurations
        if (!repository.existsById(DEFAULT)) {
            // Create and save a default payout configuration if not found
            PayoutConfigEntity defaultConfig = PayoutConfigEntity.builder()
                    .id(DEFAULT)
                    .bonusAmount(5.0)
                    .bonusThreshold(100)
                    .penaltyPerRefusal(10.0)
                    .baseDeliveryFee(3.0)
                    .weatherFee(1.0)
                    .driverCostPerKm(1.0)
                    .clientCostPerKm(2.0)
                    .build();

            repository.save(defaultConfig);
        }
    }


    public Optional<PayoutConfigEntityResponseDto> updatePayoutConfig(String id, PayoutConfigRequest updatedConfig) {
        return repository.findById(id).map(existingConfig -> {
            existingConfig.setBonusAmount(updatedConfig.getBonusAmount());
            existingConfig.setBonusThreshold(updatedConfig.getBonusThreshold());
            existingConfig.setPenaltyPerRefusal(updatedConfig.getPenaltyPerRefusal());
            existingConfig.setBaseDeliveryFee(updatedConfig.getBaseDeliveryFee());
            existingConfig.setWeatherFee(updatedConfig.getWeatherFee());
            existingConfig.setDriverCostPerKm(updatedConfig.getDriverCostPerKm());
            existingConfig.setClientCostPerKm(updatedConfig.getClientCostPerKm());

            PayoutConfigEntity savedEntity = repository.save(existingConfig);

            return new PayoutConfigEntityResponseDto(
                    savedEntity.getBonusAmount(),
                    savedEntity.getBonusThreshold(),
                    savedEntity.getPenaltyPerRefusal(),
                    savedEntity.getBaseDeliveryFee(),
                    savedEntity.getWeatherFee(),
                    savedEntity.getDriverCostPerKm(),
                    savedEntity.getClientCostPerKm()
            );
        });
    }

    // Delete a payout configuration by ID
    public boolean deletePayoutConfig(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // Fetch a payout configuration by ID
    public Optional<PayoutConfigEntity> getPayoutConfigById(String id) {
        return repository.findById(id);
    }

    // Fetch all payout configurations (if needed)
    public Iterable<PayoutConfigEntity> getAllPayoutConfigs() {
        return repository.findAll();
    }



    // Get the base delivery fee from the configuration
    public double getBaseDeliveryFee() {
        // Fetch the default configuration or throw an exception if it doesn't exist
        return getPayoutConfigById(DEFAULT).map(PayoutConfigEntity::getBaseDeliveryFee)
                .orElseThrow(() -> new IllegalStateException(DEFAULT_CONFIG_NOT_FOUND));
    }

    // Get the bonus amount from the configuration
    public double getBonusAmount() {
        // Fetch the default configuration or throw an exception if it doesn't exist
        return getPayoutConfigById(DEFAULT).map(PayoutConfigEntity::getBonusAmount)
                .orElseThrow(() -> new IllegalStateException(DEFAULT_CONFIG_NOT_FOUND));
    }

    // Get the bonus threshold from the configuration
    public int getBonusThreshold() {
        // Fetch the default configuration or throw an exception if it doesn't exist
        return getPayoutConfigById(DEFAULT).map(PayoutConfigEntity::getBonusThreshold)
                .orElseThrow(() -> new IllegalStateException(DEFAULT_CONFIG_NOT_FOUND));
    }

    // Get the penalty per refusal from the configuration
    public double getPenaltyPerRefusal() {
        // Fetch the default configuration or throw an exception if it doesn't exist
        return getPayoutConfigById(DEFAULT).map(PayoutConfigEntity::getPenaltyPerRefusal)
                .orElseThrow(() -> new IllegalStateException(DEFAULT_CONFIG_NOT_FOUND));
    }
}
