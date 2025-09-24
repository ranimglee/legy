package restaurant.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.UserPreferenceRequestDTO;
import restaurant.application.dto.UserPreferenceResponseDTO;
import restaurant.application.exception.UserPreferenceNotFoundException;
import restaurant.domain.model.UserPreference;
import restaurant.domain.repository.UserPreferenceRepository;
import shared.config.security.JwtUtil;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserPreferenceUseCase {

    private final UserPreferenceRepository prefRepo;
    private final JwtUtil jwtUtil;

    /**
     * Create or update the single preference document for this user.
     */
    public UserPreferenceResponseDTO saveOrUpdate(
            String authHeader,
            UserPreferenceRequestDTO req
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(
                authHeader.replaceFirst("^Bearer\\s+", "")
        );

        // 1) Attempt to load existing preferences
        UserPreference pref = prefRepo.findByUserId(userId)
                .orElseGet(() -> {
                    var np = new UserPreference();
                    np.setUserId(userId);
                    return np;
                });

        // 2) Overwrite with the incoming values
        pref.setCategoryNames(req.categoryNames());
        pref.setCuisineTypes(req.cuisineTypes());

        // 3) Save (upsert)
        UserPreference saved = prefRepo.save(pref);

        return toDto(saved);
    }

    public UserPreferenceResponseDTO get(String authHeader) {
        String userId = jwtUtil.extractUserIdFromAccessToken(
                authHeader.replaceFirst("^Bearer\\s+", "")
        );
        UserPreference p = prefRepo.findByUserId(userId)
                .orElse(null);

        if (p == null) {
            return new UserPreferenceResponseDTO(Collections.emptyList(), Collections.emptyList());
        }

        return toDto(p);
    }

    public void delete(String authHeader) {
        String userId = jwtUtil.extractUserIdFromAccessToken(
                authHeader.replaceFirst("^Bearer\\s+", "")
        );
        prefRepo.deleteByUserId(userId);
    }

    private UserPreferenceResponseDTO toDto(UserPreference p) {
        return new UserPreferenceResponseDTO(
                p.getCategoryNames(),
                p.getCuisineTypes()
        );
    }
}
