package com.tms.gateway.dto.request;

import lombok.Data;

@Data
public class SettingsPreferencesRequest {
    private String theme;
    private String density;
    private String language;
    private String startPage;
    private Boolean autoRefresh;
    private Boolean compactSidebar;
    private Boolean emailAlerts;
    private Boolean browserAlerts;
    private Boolean missionAlerts;
    private Boolean maintenanceAlerts;
    private Boolean weeklySummary;
}
