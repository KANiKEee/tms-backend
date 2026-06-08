package com.tms.gateway.service;

import com.tms.gateway.dto.request.SettingsPreferencesRequest;
import com.tms.gateway.dto.response.SettingsPreferencesResponse;
import com.tms.gateway.entity.User;
import com.tms.gateway.entity.UserSettings;
import com.tms.gateway.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private static final Set<String> THEMES = Set.of("light", "system", "dark");
    private static final Set<String> DENSITIES = Set.of("comfortable", "compact");
    private static final Set<String> LANGUAGES = Set.of("fr", "en");
    private static final Set<String> START_PAGES = Set.of(
            "/dashboard",
            "/missions",
            "/camions",
            "/chat",
            "/attendance",
            "/parametres"
    );

    private final UserSettingsRepository userSettingsRepository;

    public SettingsPreferencesResponse get(User user) {
        return toResponse(getOrCreate(user.getId()));
    }

    public SettingsPreferencesResponse update(User user, SettingsPreferencesRequest request) {
        UserSettings settings = getOrCreate(user.getId());
        apply(settings, request);
        return toResponse(userSettingsRepository.save(settings));
    }

    public SettingsPreferencesResponse reset(User user) {
        UserSettings settings = getOrCreate(user.getId());
        applyDefaults(settings);
        return toResponse(userSettingsRepository.save(settings));
    }

    private UserSettings getOrCreate(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> userSettingsRepository.save(defaultSettings(userId)));
    }

    private UserSettings defaultSettings(Long userId) {
        return UserSettings.builder()
                .userId(userId)
                .build();
    }

    private void apply(UserSettings settings, SettingsPreferencesRequest request) {
        if (request == null) {
            applyDefaults(settings);
            return;
        }

        settings.setTheme(validOrDefault(request.getTheme(), THEMES, "system"));
        settings.setDensity(validOrDefault(request.getDensity(), DENSITIES, "comfortable"));
        settings.setLanguage(validOrDefault(request.getLanguage(), LANGUAGES, "fr"));
        settings.setStartPage(validOrDefault(request.getStartPage(), START_PAGES, "/dashboard"));
        settings.setAutoRefresh(valueOrDefault(request.getAutoRefresh(), true));
        settings.setCompactSidebar(valueOrDefault(request.getCompactSidebar(), false));
        settings.setEmailAlerts(valueOrDefault(request.getEmailAlerts(), true));
        settings.setBrowserAlerts(valueOrDefault(request.getBrowserAlerts(), false));
        settings.setMissionAlerts(valueOrDefault(request.getMissionAlerts(), true));
        settings.setMaintenanceAlerts(valueOrDefault(request.getMaintenanceAlerts(), true));
        settings.setWeeklySummary(valueOrDefault(request.getWeeklySummary(), false));
    }

    private void applyDefaults(UserSettings settings) {
        settings.setTheme("system");
        settings.setDensity("comfortable");
        settings.setLanguage("fr");
        settings.setStartPage("/dashboard");
        settings.setAutoRefresh(true);
        settings.setCompactSidebar(false);
        settings.setEmailAlerts(true);
        settings.setBrowserAlerts(false);
        settings.setMissionAlerts(true);
        settings.setMaintenanceAlerts(true);
        settings.setWeeklySummary(false);
    }

    private String validOrDefault(String value, Set<String> allowed, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return allowed.contains(value) ? value : defaultValue;
    }

    private boolean valueOrDefault(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

    private SettingsPreferencesResponse toResponse(UserSettings settings) {
        return SettingsPreferencesResponse.builder()
                .id(settings.getId())
                .userId(settings.getUserId())
                .theme(settings.getTheme())
                .density(settings.getDensity())
                .language(settings.getLanguage())
                .startPage(settings.getStartPage())
                .autoRefresh(settings.isAutoRefresh())
                .compactSidebar(settings.isCompactSidebar())
                .emailAlerts(settings.isEmailAlerts())
                .browserAlerts(settings.isBrowserAlerts())
                .missionAlerts(settings.isMissionAlerts())
                .maintenanceAlerts(settings.isMaintenanceAlerts())
                .weeklySummary(settings.isWeeklySummary())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
