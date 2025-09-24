package payment.infrastructure.persistence.notificationSetting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notification_settings")
public class NotificationSettingDocument {
        @Id
        private String id;
        private int notificationDelayInDays;

        public static NotificationSettingDocument fromDomain(payment.domain.model.NotificationSetting setting) {
                return new NotificationSettingDocument(
                        setting.getId(),
                        setting.getNotificationDelayInDays()
                );
        }

        public payment.domain.model.NotificationSetting toDomain() {
                return new payment.domain.model.NotificationSetting(
                        id,
                        notificationDelayInDays
                );
        }

}
