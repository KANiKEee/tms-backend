package com.tms.warehouse.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardWarehouseResponse {
    private long totalEntrepots;
    private long totalProduits;
    private long totalZones;
    private long alertesStock;          // products below threshold
    private long mouvementsAujourdhui;  // movements today
    private List<StockResponse> produitsEnAlerte;
    private List<MouvementResponse> derniersMouvements;
}
