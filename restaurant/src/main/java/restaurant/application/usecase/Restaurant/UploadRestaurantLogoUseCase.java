package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import restaurant.domain.model.Restaurant;
import restaurant.domain.service.CloudinaryService;
import restaurant.domain.service.RestaurantDomainService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UploadRestaurantLogoUseCase {

    private final RestaurantDomainService restaurantDomainService;
    private final CloudinaryService cloudinaryService;

    public String execute(String restaurantId, MultipartFile logoFile) throws IOException {
        Restaurant restaurant = restaurantDomainService.getRestaurantById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant introuvable avec l'identifiant : " + restaurantId));

        String logoUrl = cloudinaryService.uploadFile(logoFile);
        restaurant.setLogo(logoUrl);
        restaurantDomainService.updateRestaurant(restaurant);

        return logoUrl;
    }
}
