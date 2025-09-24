package payment.infrastructure.persistence.notificationSetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import payment.domain.model.NotificationSetting;
import payment.domain.repository.NotificationSettingRepository;

import java.util.List;
import java.util.stream.Collectors;
@Repository

public class NotificationSettingAdapter implements NotificationSettingRepository {
    private final NotificationSettingMongoRepository mongoRepository;

    public NotificationSettingAdapter(NotificationSettingMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public List<NotificationSetting> findAll() {
        return mongoRepository.findAll().stream()
                .map(NotificationSettingDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(NotificationSetting setting) {
        mongoRepository.save(NotificationSettingDocument.fromDomain(setting));
    }
}
