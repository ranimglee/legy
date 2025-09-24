package restaurant.application.usecase.Promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;

@Service
@RequiredArgsConstructor
public class DeletePromotionUseCase {
    private final PromotionRepository repo;

    public void execute(String id) {
        Promotion p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        repo.delete(p);
    }
}

