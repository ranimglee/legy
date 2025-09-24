package restaurant.infrastructure.adapter.Rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.config.JwtConfig;
import restaurant.domain.model.Comment;
import restaurant.infrastructure.service.CommentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtConfig jwtConfig;

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(org.apache.http.HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtConfig.extractUserIdFromAccessToken(authHeader.substring(7));
        }
        throw new RuntimeException("JWT token missing or invalid.");
    }

    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestParam String targetId,
                                              @RequestParam String targetType,
                                              @RequestParam String content,
                                              HttpServletRequest request) {
        String userId = extractUserId(request);
        Comment saved = commentService.addComment(userId, targetId, targetType, content);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/reply")
    public ResponseEntity<Comment> replyToComment(@RequestParam String parentId,
                                                  @RequestParam String content,
                                                  HttpServletRequest request) {
        String userId = extractUserId(request);
        Comment saved = commentService.addReply(userId, parentId, content);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{targetId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String targetId,
                                                     @RequestParam String targetType) {
        List<Comment> comments = commentService.getComments(targetId, targetType);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/replies/{parentId}")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable String parentId) {
        List<Comment> replies = commentService.getReplies(parentId);
        return ResponseEntity.ok(replies);
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> likeComment(@PathVariable String commentId,
                                              HttpServletRequest request) {
        String userId = extractUserId(request);
        commentService.likeComment(userId, commentId);
        return ResponseEntity.ok("Comment liked.");
    }

    @GetMapping("/{commentId}/likes")
    public ResponseEntity<Map<String, Long>> getCommentLikes(@PathVariable String commentId) {
        long count = commentService.getCommentLikeCount(commentId);
        return ResponseEntity.ok(Map.of("likes", count));
    }
}
