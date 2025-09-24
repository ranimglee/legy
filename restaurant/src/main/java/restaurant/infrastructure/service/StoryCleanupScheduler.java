package restaurant.infrastructure.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import restaurant.domain.model.Story;
import restaurant.domain.repository.StoryRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class StoryCleanupScheduler {

    private final StoryRepository storyRepository;
    private final S3Client s3Client;
    private final String bucketName = "legy-application-bucket";

    @Scheduled(fixedRate = 30000) // every 30 seconds for test
    public void cleanupExpiredStories() {
        Instant cutoff = Instant.now().minusSeconds(86400);
        List<Story> expired = storyRepository.findByUploadedAtBefore(cutoff);

        for (Story story : expired) {
            if (story.getS3Key().startsWith("stories/")) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(story.getS3Key())
                        .build());

                storyRepository.deleteById(story.getId());
                System.out.println("✅ Deleted expired story: " + story.getId());
            }
        }
    }
}
