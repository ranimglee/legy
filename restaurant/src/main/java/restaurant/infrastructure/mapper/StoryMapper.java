package restaurant.infrastructure.mapper;

import restaurant.domain.model.Story;
import restaurant.infrastructure.Document.MongoStory;

public class StoryMapper {

    public static MongoStory toMongo(Story story) {
        return MongoStory.builder()
                .id(story.getId())
                .userId(story.getUserId())
                .restaurantId(story.getRestaurantId())
                .s3Key(story.getS3Key())
                .url(story.getUrl())
                .uploadedAt(story.getUploadedAt())
                .viewCount(story.getViewCount())
                .build();
    }

    public static Story toDomain(MongoStory mongoStory) {
        return Story.builder()
                .id(mongoStory.getId())
                .userId(mongoStory.getUserId())
                .restaurantId(mongoStory.getRestaurantId())
                .s3Key(mongoStory.getS3Key())
                .url(mongoStory.getUrl())
                .uploadedAt(mongoStory.getUploadedAt())
                .viewCount(mongoStory.getViewCount())
                .build();
    }
}
