package user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.application.dto.in.EvaluateLivreurResponse;
import user.application.dto.out.AverageRating;
import user.application.dto.out.AverageRatingResponse;
import user.application.dto.out.EvaluateLivreurRequest;
import user.application.exception.LivreurNotFoundException;
import user.domain.model.Evaluation;
import user.domain.model.LivreurEntity;
import user.domain.model.UserEntity;
import user.domain.repository.EvaluationRepository;
import user.domain.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evalRepo;
    private final UserRepository userRepo;

    private void assertLivreurExists(String livreurId) {
        UserEntity user = userRepo.findById(livreurId)
                .orElseThrow(() -> new LivreurNotFoundException(livreurId));

        if (!(user instanceof LivreurEntity)) {
            throw new LivreurNotFoundException(livreurId);
        }
    }

    public EvaluateLivreurResponse evaluate(
            String livreurId,
            String clientId,
            EvaluateLivreurRequest req
    ) {
        assertLivreurExists(livreurId);
        Instant now = Instant.now();

        // upsert logic as before…
        Optional<Evaluation> existing = evalRepo.findByLivreurIdAndClientId(livreurId, clientId);
        Evaluation toSave = existing
                .map(old -> old.toBuilder()
                        .rating(req.rating())
                        .comment(req.comment())
                        .createdAt(now)
                        .build()
                )
                .orElseGet(() -> Evaluation.builder()
                        .id(UUID.randomUUID().toString())
                        .livreurId(livreurId)
                        .clientId(clientId)
                        .rating(req.rating())
                        .comment(req.comment())
                        .createdAt(now)
                        .build()
                );

        Evaluation saved = evalRepo.save(toSave);
        return new EvaluateLivreurResponse(
                saved.getId(),
                saved.getLivreurId(),
                saved.getClientId(),
                saved.getRating(),
                saved.getComment(),
                saved.getCreatedAt().toString()
        );
    }

    public List<EvaluateLivreurResponse> getEvaluationsForLivreur(String livreurId) {
        assertLivreurExists(livreurId);

        return evalRepo.findByLivreurId(livreurId).stream()
                .map(e -> new EvaluateLivreurResponse(
                        e.getId(),
                        e.getLivreurId(),
                        e.getClientId(),
                        e.getRating(),
                        e.getComment(),
                        e.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }


    public AverageRating getAverageRating(String livreurId, String period) {
        assertLivreurExists(livreurId);

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        LocalDate startDate = switch (period.toLowerCase()) {
            case "daily" -> today;
            case "weekly" -> today.minusDays(6);
            case "monthly" -> today.minusDays(29);
            default -> null;
        };

        List<Evaluation> evaluations = evalRepo.findByLivreurId(livreurId).stream()
                .filter(e -> {
                    LocalDate date = e.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate();
                    boolean included = startDate == null || !date.isBefore(startDate);
                    return included;
                })
                .toList();


        double avg = evaluations.stream()
                .mapToInt(Evaluation::getRating)
                .average()
                .orElse(0.0);

        return new AverageRating(
                livreurId,
                avg,
                evaluations.size()
        );
    }

}
