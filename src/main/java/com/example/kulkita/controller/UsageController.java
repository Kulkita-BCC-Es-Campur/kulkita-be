package com.example.kulkita.controller;

import com.example.kulkita.dto.request.UsageRecordRequest;
import com.example.kulkita.dto.response.MessageResponse;
import com.example.kulkita.dto.response.UsageHistoryResponse;
import com.example.kulkita.security.services.UserPrincipal;
import com.example.kulkita.service.UsageService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static com.example.kulkita.service.impl.AuthServiceImpl.log;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    private final UsageService usageService;

    public UsageController(UsageService usageService) {
        this.usageService = usageService;
    }

    @PostMapping("/record")
    public ResponseEntity<MessageResponse> recordUsage(
            @Valid @RequestBody UsageRecordRequest request,
            Authentication authentication) {
        String userIdentifier = getUserIdentifierFromAuthentication(authentication);
        MessageResponse response = usageService.recordUsage(request, userIdentifier);
        return ResponseEntity.ok(response);
    }

    private String getUserIdentifierFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication is required");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            String email = userPrincipal.getEmail();
            log.debug("Got email from UserPrincipal: {}", email);
            return email;
        }

        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            log.debug("Got username from UserDetails: {}", username);
            return username;
        }

        String name = authentication.getName();
        log.debug("Got name from Authentication: {}", name);
        return name;
    }

    @GetMapping("/history/batch/{batchCode}")
    public ResponseEntity<List<UsageHistoryResponse>> getBatchUsageHistory(
            @PathVariable String batchCode) {
        List<UsageHistoryResponse> history = usageService.getUsageHistory(batchCode);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history")
    public ResponseEntity<List<UsageHistoryResponse>> getUsageHistoryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        List<UsageHistoryResponse> history = usageService.getUsageHistoryByDateRange(startInstant, endInstant);
        return ResponseEntity.ok(history);
    }
}