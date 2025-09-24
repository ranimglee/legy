package restaurant.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import restaurant.application.dto.Product.ProductListDTO;
import restaurant.application.usecase.Product.GetPopularProductsByCuisineUseCase;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmupScheduler {

    private final GetPopularProductsByCuisineUseCase useCase;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void scheduledWarmup() {
        log.info("⏰ Scheduled cache warmup started...");
        warmupAll();
    }

    private void warmupAll() {
        for (MainCuisineType main : MainCuisineType.values()) {
            refreshCuisineCache(main, null);

            if (main == MainCuisineType.INTERNATIONALE) {
                for (InternationalCuisine sub : InternationalCuisine.values()) {
                    refreshCuisineCache(main, sub);
                }
            }
        }
        log.info("✅ All popular products cache warmed.");
    }

    private void refreshCuisineCache(MainCuisineType main, InternationalCuisine sub) {
        try {
            int pageSize = 12;
            int pagesToWarm = 3;

            for (int page = 0; page < pagesToWarm; page++) {
                ProductListDTO result = useCase.execute(main, sub, page, pageSize);
                log.info("✅ Refreshed cache for {}{} [page {}] ({} products)",
                        main,
                        (sub != null ? " - " + sub : ""),
                        page,
                        result.content().size());

                // Stop early if last page has fewer items (no more data)
                if (result.content().size() < pageSize) break;
            }
        } catch (Exception e) {
            log.warn("⚠️ Failed to refresh cache for {}{}: {}",
                    main,
                    (sub != null ? " - " + sub : ""),
                    e.getMessage());
        }
    }

}
