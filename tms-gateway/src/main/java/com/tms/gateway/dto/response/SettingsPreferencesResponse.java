package com.tms.gateway.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SettingsPreferencesResponse {
    private Long id;
    private Long userId;
    private String theme;
    private String density;
    private String language;
    private String startPage;
    private boolean autoRefresh;
    private boolean compactSidebar;
    private boolean emailAlerts;
    private boolean browserAlerts;
    private boolean missionAlerts;
    private boolean maintenanceAlerts;
    private boolean weeklySummary;
    private LocalDateTime updatedAt;
}
