package restaurant.application.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import restaurant.application.dto.Story.StoryResponseDTO;
import restaurant.domain.model.Story;
import shared.domain.service.RestaurantQueryService;
import shared.dto.RestaurantInfoDTO;
@Component
@RequiredArgsConstructor
@Slf4j
public class StoryMapper {

    private final RestaurantQueryService restaurantQueryService;

    public StoryResponseDTO toDTO(Story story) {
        log.debug("📝 Mapping Story to DTO: Story ID = {}, User ID = {}, Restaurant ID = {}",
                story.getId(), story.getUserId(), story.getRestaurantId());

        if (story.getRestaurantId() == null) {
            log.warn("⚠️ Story with ID {} has null Restaurant ID. Possible data inconsistency!", story.getId());
        }

        // Fetching Restaurant Info
        RestaurantInfoDTO restoInfo = restaurantQueryService.getRestaurantInfoById(story.getRestaurantId());
        log.debug("🏢 Fetched Restaurant Info for Restaurant ID {}: Name = '{}', Logo URL = '{}'",
                story.getRestaurantId(), restoInfo.name(), restoInfo.logo());

        return StoryResponseDTO.builder()
                .id(story.getId())
                .userId(story.getUserId())
                .s3Key(story.getS3Key())
                .url(story.getUrl())
                .uploadedAt(story.getUploadedAt())
                .viewCount(story.getViewCount())
                .restaurantId(restoInfo.restaurantId())
                .restaurantName(restoInfo.name())
                .restaurantLogo(restoInfo.logo())
                .build();
    }
}
