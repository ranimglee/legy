package restaurant.application.usecase.Promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;
import restaurant.domain.service.CloudinaryService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UploadPromotionImageUseCase {
    private final PromotionRepository promotionRepository;
    private final CloudinaryService cloudinaryService;

    public String execute(String promotionId, MultipartFile image) throws IOException {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        String imageUrl = cloudinaryService.uploadFile(image);
        promotion.setImageUrl(imageUrl);
        promotionRepository.save(promotion);

        return imageUrl;
    }
}
