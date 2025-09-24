package user.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shared.config.security.JwtUtil;
import user.application.dto.in.EvaluateLivreurResponse;
import user.application.dto.out.AverageRating;
import user.application.dto.out.AverageRatingResponse;
import user.application.dto.out.EvaluateLivreurRequest;
import user.application.exception.InvalidTokenException;
import user.application.exception.MissingTokenException;
import user.application.service.EvaluationService;

import java.util.List;

@RestController
@RequestMapping("/api/livreurs/{livreurId}/evaluations")
@RequiredArgsConstructor
@Tag(name = "Livreur Evaluation", description = "Endpoints for evaluating and retrieving ratings for livreurs")

public class EvaluationController {

    private final EvaluationService evaluationService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "Evaluate a livreur",
            description = "Submit an evaluation for a livreur by a client. Requires authentication."
    )
    @PostMapping
    public ResponseEntity<EvaluateLivreurResponse> evaluate(
            @PathVariable String livreurId,
            @Valid @RequestBody EvaluateLivreurRequest request,
            HttpServletRequest httpRequest
    ) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MissingTokenException();
        }

        String token = authHeader.substring(7);
        String clientId;
        try {
            clientId = jwtUtil.extractUserIdFromAccessToken(token);
        } catch (Exception ex) {
            throw new InvalidTokenException("Cannot parse or validate JWT");
        }

        EvaluateLivreurResponse resp = evaluationService.evaluate(
                livreurId,
                clientId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

    @Operation(
            summary = "List all evaluations for a livreur",
            description = "Returns a list of all evaluations submitted for a specific livreur."
    )
    @GetMapping
    public List<EvaluateLivreurResponse> list(
            @PathVariable String livreurId
    ) {
        return evaluationService.getEvaluationsForLivreur(livreurId);
    }

    @Operation(
            summary = "Get average rating",
            description = "Returns the average rating of a livreur, optionally filtered by time period (e.g., 'daily', 'weekly', 'all')."
    )
    @GetMapping("/average")
    public AverageRating average(
            @PathVariable String livreurId,
            @RequestParam(defaultValue = "all") String period
    ) {
        return evaluationService.getAverageRating(livreurId, period);
    }
}
