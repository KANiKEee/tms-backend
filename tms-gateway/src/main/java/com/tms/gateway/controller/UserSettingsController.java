package com.tms.gateway.controller;

import com.tms.gateway.dto.request.SettingsPreferencesRequest;
import com.tms.gateway.dto.response.SettingsPreferencesResponse;
import com.tms.gateway.entity.User;
import com.tms.gateway.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping
    public ResponseEntity<SettingsPreferencesResponse> get(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userSettingsService.get(user));
    }

    @PutMapping
    public ResponseEntity<SettingsPreferencesResponse> update(
            @AuthenticationPrincipal User user,
            @RequestBody SettingsPreferencesRequest request) {
        return ResponseEntity.ok(userSettingsService.update(user, request));
    }

    @PutMapping("/reset")
    public ResponseEntity<SettingsPreferencesResponse> reset(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userSettingsService.reset(user));
    }
}
