package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import restaurant.application.dto.Product.ImageUploadResponse;
import restaurant.domain.service.ProductImageStorageService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UploadProductImageUseCase {

    private final ProductImageStorageService storageService;

    public ImageUploadResponse execute(MultipartFile image) throws IOException {
        String url = storageService.upload(image);
        return new ImageUploadResponse(url);
    }
}
