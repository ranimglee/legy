package delivery.adapters.rest;

import delivery.application.dto.out.LivreurPerformanceDTO;
import delivery.application.usecase.GetLivreurPerformanceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livreurs")
@RequiredArgsConstructor
public class  LivreurPerformanceController {

    private final GetLivreurPerformanceUseCase getPerformanceUseCase;

    @GetMapping("/{livreurId}/performance")
    public LivreurPerformanceDTO getPerformance(
            @PathVariable String livreurId,
            @RequestParam(defaultValue = "all") String period
    ) {
        return getPerformanceUseCase.handle(livreurId, period);
    }

}
