package user.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import user.application.dto.in.UpdateFinancialProfileRequest;
import user.application.dto.in.UpdateModeratorProfileRequest;
import user.application.dto.out.FinancialProfileResponse;
import user.application.dto.out.ModeratorProfileResponse;
import user.application.service.FinancierService;
import user.infrastructure.security.JwtUtilImpl;

import java.util.Map;

@RestController
@RequestMapping("/api/financier")
@RequiredArgsConstructor
@Tag(name = "financier", description = "Endpoints for financial profile")
@Slf4j
public class FinancialController {

    private final JwtUtilImpl jwtUtil;
    private final FinancierService financierService;

    @Operation(summary = "Update financial profile", description = "Updates the profile information of the authenticated financial.")
    @PutMapping("/profile/update")
    public ResponseEntity<FinancialProfileResponse> updateMyProfile(
            @RequestBody UpdateFinancialProfileRequest request,
            HttpServletRequest httpRequest
    ) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmailFromAccessToken(token);

        return ResponseEntity.ok(financierService.updateProfile(email, request));
    }

}
