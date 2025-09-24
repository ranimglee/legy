package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import restaurant.application.dto.Story.StoryResponseDTO;
import restaurant.application.mapper.StoryMapper;
import restaurant.domain.model.Restaurant;
import restaurant.domain.model.Story;
import restaurant.domain.repository.RestaurantRepository;
import restaurant.domain.repository.StoryRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoryReelService {

    private final S3Client s3Client;
    private final StoryRepository storyRepository;
    private final RestaurantRepository restaurantRepository;
    private final StoryMapper storyMapper;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public Story uploadMedia(String userId, MultipartFile file, String type) throws IOException {
        log.debug("🚀 Starting uploadMedia with userId={}, fileName={}, type={}", userId, file.getOriginalFilename(), type);

        if (!type.equals("stories") && !type.equals("reels")) {
            log.error("❌ Invalid media type: {}", type);
            throw new IllegalArgumentException("Invalid type. Must be 'stories' or 'reels'.");
        }

        String key = type + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        log.debug("🗝️ Generated S3 key: {}", key);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                file.getInputStream(), file.getSize()
        ));

        String url = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key)).toExternalForm();
        log.debug("✅ File uploaded to S3. URL: {}", url);

        Restaurant restaurant = restaurantRepository
                .findByCreatedBy(userId)
                .orElseThrow(() -> {
                    log.error("⚠️ No restaurant found for userId={}", userId);
                    return new RuntimeException("Aucun restaurant associé à l’utilisateur " + userId);
                });

        log.debug("🏢 Associated restaurant found: {}", restaurant.getId());

        Story story = Story.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .restaurantId(restaurant.getId())
                .s3Key(key)
                .url(url)
                .uploadedAt(Instant.now())
                .build();

        Story savedStory = storyRepository.save(story);
        log.debug("💾 Story saved in DB: {}", savedStory.getId());

        return savedStory;
    }

    public List<Story> getMediaByUser(String userId, String type) {
        log.debug("🔍 Fetching media for userId={} and type={}", userId, type);
        return storyRepository.findByUserId(userId).stream()
                .filter(story -> story.getS3Key().startsWith(type + "/"))
                .collect(Collectors.toList());
    }

    public List<Story> getAllMedia(String type) {
        log.debug("📦 Fetching all media of type={}", type);
        return storyRepository.findAll().stream()
                .filter(story -> story.getS3Key().startsWith(type + "/"))
                .collect(Collectors.toList());
    }

    public boolean deleteMediaById(String mediaId) {
        log.debug("🗑️ Attempting to delete media with ID={}", mediaId);
        Optional<Story> optionalStory = storyRepository.findById(mediaId);
        if (optionalStory.isPresent()) {
            Story story = optionalStory.get();
            log.debug("📄 Media found. S3 Key={}", story.getS3Key());

            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(story.getS3Key())
                    .build());

            storyRepository.deleteById(story.getId());
            log.info("✅ Deleted media: {}", story.getId());
            return true;
        } else {
            log.warn("⚠️ Media not found: {}", mediaId);
            return false;
        }
    }

    public List<Story> getMediaByRestaurantIds(List<String> restaurantIds, String type) {
        log.debug("🔍 Fetching media for restaurantIds={} and type={}", restaurantIds, type);
        return storyRepository.findByUserIdIn(restaurantIds).stream()
                .filter(story -> story.getS3Key().startsWith(type + "/"))
                .collect(Collectors.toList());
    }

    public List<Story> getStoriesOfFollowedRestaurants(Set<String> restaurantIds, String type) {
        log.debug("👥 Fetching stories for followed restaurants: {}, type={}", restaurantIds, type);
        return storyRepository.findByUserIdIn(List.copyOf(restaurantIds))
                .stream()
                .filter(story -> story.getS3Key().startsWith(type + "/"))
                .collect(Collectors.toList());
    }

    public List<StoryResponseDTO> getStoriesByUser(String userId, String type) {
        log.debug("📄 Fetching StoryResponseDTOs for userId={}, type={}", userId, type);
        return storyRepository.findByUserId(userId).stream()
                .filter(story -> story.getS3Key().startsWith(type + "/"))
                .map(storyMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<StoryResponseDTO> getAllStories(String type) {
        log.debug("🌍 Fetching all StoryResponseDTOs of type={}", type);
        return storyRepository.findAll().stream()
                .filter(story -> story.getS3Key().startsWith(type + "/"))
                .map(storyMapper::toDTO)
                .collect(Collectors.toList());
    }



    public List<StoryResponseDTO> getReelsByUser(String userId, String type) {
        log.debug("📄 Fetching StoryResponseDTOs for userId={}, type={}", userId, type);
        return storyRepository.findByUserId(userId).stream()
                .filter(story -> story.getS3Key().startsWith(type + "/"))
                .map(storyMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<StoryResponseDTO> getAllReels(String type) {
        log.debug("🌍 Fetching all StoryResponseDTOs of type={}", type);
        return storyRepository.findAll().stream()
                .filter(story -> story.getS3Key().startsWith(type + "/"))
                .map(storyMapper::toDTO)
                .collect(Collectors.toList());
    }

    public StoryResponseDTO getStoryResponseDTO(Story story) {
        log.debug("📝 Mapping Story to StoryResponseDTO for story ID={}", story.getId());
        return storyMapper.toDTO(story);
    }
}
