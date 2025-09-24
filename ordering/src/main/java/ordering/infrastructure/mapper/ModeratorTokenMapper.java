package ordering.infrastructure.mapper;

import ordering.domain.model.ModeratorToken;
import ordering.infrastructure.Document.ModeratorTokenDocument;

public class ModeratorTokenMapper {

    public static ModeratorToken toDomain(ModeratorTokenDocument document) {
        if (document == null) {
            return null;
        }
        return new ModeratorToken(
                document.getId(),
                document.getModeratorId(),
                document.getFcmToken(),
                document.getCreatedAt()
        );
    }

    public static ModeratorTokenDocument toDocument(ModeratorToken domain) {
        if (domain == null) {
            return null;
        }
        return ModeratorTokenDocument.builder()
                .id(domain.getId())
                .moderatorId(domain.getModeratorId())
                .fcmToken(domain.getFcmToken())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
