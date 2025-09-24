package payment.domain.repository;

import payment.domain.model.NotificationSetting;

import java.util.List;

public interface NotificationSettingRepository {

    List<NotificationSetting> findAll();

    void save(NotificationSetting setting);
}
