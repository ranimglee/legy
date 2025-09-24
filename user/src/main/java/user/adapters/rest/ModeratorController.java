package user.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import user.application.dto.in.ClientProfileResponse;
import user.application.dto.in.UpdateModeratorProfileRequest;
import user.application.dto.out.ModeratorProfileResponse;
import user.application.dto.out.UpdateClientProfileRequest;
import user.application.service.ModerateurService;
import user.infrastructure.security.JwtUtilImpl;


@RestController
@RequestMapping("/api/moderateur")
@RequiredArgsConstructor
@Tag(name = "moderateur", description = "Endpoints for moderator profile")
@Slf4j
public class ModeratorController {
    private final JwtUtilImpl jwtUtil;
    private final ModerateurService moderateurService;

    @Operation(summary = "Update moderator profile", description = "Updates the profile information of the authenticated moderator.")
    @PutMapping("/profile/update")
    public ResponseEntity<ModeratorProfileResponse> updateMyProfile(
            @RequestBody UpdateModeratorProfileRequest request,
            HttpServletRequest httpRequest
    ) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmailFromAccessToken(token);

        return ResponseEntity.ok(moderateurService.updateProfile(email, request));
    }
}
