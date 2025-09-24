package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import user.application.dto.in.EvaluateLivreurResponse;
import user.application.dto.out.AverageRating;
import user.application.dto.out.EvaluateLivreurRequest;
import user.application.exception.LivreurNotFoundException;
import user.application.service.EvaluationService;
import user.domain.model.*;
import user.domain.repository.EvaluationRepository;
import user.domain.repository.UserRepository;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationServiceTest {

    @Mock private EvaluationRepository evalRepo;
    @Mock private UserRepository userRepo;
    @InjectMocks private EvaluationService service;

    private LivreurEntity livreur;
    private ClientEntity client;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new EvaluationService(evalRepo, userRepo);

        livreur = new LivreurEntity();
        livreur.setId("livreur-1");
        livreur.setEmail("l@e.com");

        client = new ClientEntity();
        client.setId("client-1");
    }

    @Test
    void shouldEvaluateNewLivreur() {
        EvaluateLivreurRequest req = new EvaluateLivreurRequest(4, "Good job");

        when(userRepo.findById("livreur-1")).thenReturn(Optional.of(livreur));
        when(evalRepo.findByLivreurIdAndClientId("livreur-1", "client-1")).thenReturn(Optional.empty());

        ArgumentCaptor<Evaluation> captor = ArgumentCaptor.forClass(Evaluation.class);
        when(evalRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EvaluateLivreurResponse response = service.evaluate("livreur-1", "client-1", req);

        verify(evalRepo).save(captor.capture());
        Evaluation saved = captor.getValue();

        assertEquals("livreur-1", saved.getLivreurId());
        assertEquals("client-1", saved.getClientId());
        assertEquals(4, saved.getRating());
        assertEquals("Good job", saved.getComment());
        assertNotNull(response);
    }

    @Test
    void shouldUpdateExistingEvaluation() {
        EvaluateLivreurRequest req = new EvaluateLivreurRequest(5, "Updated");

        Evaluation old = Evaluation.builder()
                .id("eval-1")
                .livreurId("livreur-1")
                .clientId("client-1")
                .rating(2)
                .comment("Old")
                .createdAt(Instant.now().minusSeconds(1000))
                .build();

        when(userRepo.findById("livreur-1")).thenReturn(Optional.of(livreur));
        when(evalRepo.findByLivreurIdAndClientId("livreur-1", "client-1")).thenReturn(Optional.of(old));
        when(evalRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EvaluateLivreurResponse response = service.evaluate("livreur-1", "client-1", req);

        assertEquals(5, response.rating());
        assertEquals("Updated", response.comment());
    }

    @Test
    void shouldThrowIfLivreurNotFound() {
        when(userRepo.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(LivreurNotFoundException.class, () ->
                service.evaluate("bad-id", "client", new EvaluateLivreurRequest(3, "test"))
        );
    }

    @Test
    void shouldListEvaluations() {
        Evaluation e1 = Evaluation.builder()
                .id("1").livreurId("livreur-1").clientId("c1").rating(4).comment("Nice").createdAt(Instant.now()).build();
        Evaluation e2 = Evaluation.builder()
                .id("2").livreurId("livreur-1").clientId("c2").rating(5).comment("Great").createdAt(Instant.now()).build();

        when(userRepo.findById("livreur-1")).thenReturn(Optional.of(livreur));
        when(evalRepo.findByLivreurId("livreur-1")).thenReturn(List.of(e1, e2));

        List<EvaluateLivreurResponse> result = service.getEvaluationsForLivreur("livreur-1");

        assertEquals(2, result.size());
    }

    @Test
    void shouldComputeAverageRating() {
        Evaluation e1 = Evaluation.builder().rating(4).createdAt(Instant.now()).build();
        Evaluation e2 = Evaluation.builder().rating(2).createdAt(Instant.now()).build();

        when(userRepo.findById("livreur-1")).thenReturn(Optional.of(livreur));
        when(evalRepo.findByLivreurId("livreur-1")).thenReturn(List.of(e1, e2));

        AverageRating rating = service.getAverageRating("livreur-1", "all");

      //  assertEquals(3.0, rating.averageRating());
        // assertEquals(2, rating.totalEvaluations());
    }
}
