package restaurant.application.dto.Restaurant;

public record ToggleReactionRequestDTO(String reaction) {
    public enum Reaction { LIKE, DISLIKE }
}

