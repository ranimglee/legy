package ordering.infrastructure.persistence;

import ordering.domain.model.ModeratorToken;
import ordering.domain.repository.ModeratorTokenRepository;

import lombok.RequiredArgsConstructor;
import ordering.infrastructure.Document.ModeratorTokenDocument;

import ordering.infrastructure.mapper.ModeratorTokenMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ModeratorTokenRepositoryImpl implements ModeratorTokenRepository {

    private final MongoModeratorTokenRepository mongoRepo;
    @Override
    public void saveToken(String moderatorId, String fcmToken) {
        // Create a new document with the provided moderatorId and fcmToken
        ModeratorTokenDocument doc = new ModeratorTokenDocument();
        doc.setModeratorId(moderatorId);
        doc.setFcmToken(fcmToken);
        doc.setCreatedAt(Instant.now());

        // Check if a token already exists for the moderatorId
        mongoRepo.findByModeratorId(moderatorId)
                .ifPresent(existing -> doc.setId(existing.getId()));

        // Save (update if ID exists, insert if not)
        mongoRepo.save(doc);
    }


    @Override
    public String getToken(String moderatorId) {
        return mongoRepo.findByModeratorId(moderatorId)
                .map(ModeratorTokenDocument::getFcmToken)
                .orElse(null);
    }

    @Override
    public void deleteToken(String moderatorId) {
        mongoRepo.findByModeratorId(moderatorId)
                .ifPresent(doc -> mongoRepo.deleteById(doc.getId()));
    }



    @Override
    public List<ModeratorToken> findAll() {
        return mongoRepo.findAll().stream()
                .map(ModeratorTokenMapper::toDomain)
                .collect(Collectors.toList());
    }


}