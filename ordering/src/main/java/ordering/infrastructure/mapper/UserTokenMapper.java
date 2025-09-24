package ordering.infrastructure.mapper;

import ordering.domain.model.UserToken;
import ordering.infrastructure.Document.UserTokenDocument;
import org.springframework.stereotype.Component;


@Component
public class UserTokenMapper {

    /**
     * Converts a {@link UserToken} domain model to a {@link UserTokenDocument}.
     *
     * @param userToken the domain model to convert
     * @return the corresponding {@link UserTokenDocument}, or null if the input is null
     */
    public UserTokenDocument toDocument(UserToken userToken) {
        if (userToken == null) {
            return null;
        }
        return new UserTokenDocument(
                userToken.getId(),
                userToken.getUserId(),
                userToken.getFcmToken(),
                userToken.getCreatedAt()
        );
    }

    /**
     * Converts a {@link UserTokenDocument} to a {@link UserToken} domain model.
     *
     * @param document the infrastructure document to convert
     * @return the corresponding {@link UserToken}, or null if the input is null
     */
    public UserToken toDomain(UserTokenDocument document) {
        if (document == null) {
            return null;
        }
        return new UserToken(
                document.getId(),
                document.getUserId(),
                document.getFcmToken(),
                document.getCreatedAt()
        );
    }
}
