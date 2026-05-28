package com.tms.gateway.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Builder.Default
    @Column(nullable = false)
    private String theme = "system";

    @Builder.Default
    @Column(nullable = false)
    private String density = "comfortable";

    @Builder.Default
    @Column(nullable = false)
    private String language = "fr";

    @Builder.Default
    @Column(name = "start_page", nullable = false)
    private String startPage = "/dashboard";

    @Builder.Default
    @Column(name = "auto_refresh", nullable = false)
    private boolean autoRefresh = true;

    @Builder.Default
    @Column(name = "compact_sidebar", nullable = false)
    private boolean compactSidebar = false;

    @Builder.Default
    @Column(name = "email_alerts", nullable = false)
    private boolean emailAlerts = true;

    @Builder.Default
    @Column(name = "browser_alerts", nullable = false)
    private boolean browserAlerts = false;

    @Builder.Default
    @Column(name = "mission_alerts", nullable = false)
    private boolean missionAlerts = true;

    @Builder.Default
    @Column(name = "maintenance_alerts", nullable = false)
    private boolean maintenanceAlerts = true;

    @Builder.Default
    @Column(name = "weekly_summary", nullable = false)
    private boolean weeklySummary = false;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
