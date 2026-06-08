package com.tms.mission.service;

import com.tms.mission.dto.MissionRequest;
import com.tms.mission.dto.MissionResponse;
import com.tms.mission.entity.MissionEntity;
import com.tms.mission.repository.MissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    private MissionService missionService;

    @BeforeEach
    void setUp() {
        missionService = new MissionService(missionRepository);
        lenient().when(missionRepository.save(any(MissionEntity.class))).thenAnswer(invocation -> {
            MissionEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
    }

    @Test
    void createMissionCalculatesProductVolumeAndWeight() {
        MissionRequest request = validRequest();
        request.setColisNombre(12);
        request.setProduitId(9L);
        request.setProduitNom("Carton pieces");
        request.setProduitVolumeUnitaire(0.25);
        request.setProduitPoidsUnitaire(8.5);

        MissionResponse response = missionService.createMission(request);

        assertEquals(3.0, response.getVolumeTotal());
        assertEquals(102.0, response.getPoidsTotal());
        assertEquals(9L, response.getProduitId());
        assertEquals("EN_ATTENTE", response.getStatut());

        ArgumentCaptor<MissionEntity> captor = ArgumentCaptor.forClass(MissionEntity.class);
        org.mockito.Mockito.verify(missionRepository).save(captor.capture());
        assertEquals("Carton pieces", captor.getValue().getProduitNom());
    }

    @Test
    void createMissionRejectsInvalidDateRange() {
        MissionRequest request = validRequest();
        request.setDateFin(request.getDateDebut().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> missionService.createMission(request));
    }

    @Test
    void startMissionChangesWaitingMissionToInProgress() {
        MissionEntity entity = MissionEntity.builder()
                .id(3L)
                .chauffeurId(2L)
                .depart("Alger")
                .destination("Oran")
                .dateDebut(LocalDate.of(2026, 6, 8))
                .dateFin(LocalDate.of(2026, 6, 9))
                .statut("EN_ATTENTE")
                .build();
        when(missionRepository.findById(3L)).thenReturn(Optional.of(entity));

        MissionResponse response = missionService.startMission(3L);

        assertEquals("EN_COURS", response.getStatut());
    }

    @Test
    void completeMissionRejectsMissionThatWasNotStarted() {
        MissionEntity entity = MissionEntity.builder()
                .id(3L)
                .chauffeurId(2L)
                .depart("Alger")
                .destination("Oran")
                .dateDebut(LocalDate.of(2026, 6, 8))
                .dateFin(LocalDate.of(2026, 6, 9))
                .statut("EN_ATTENTE")
                .build();
        when(missionRepository.findById(3L)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> missionService.completeMission(3L));
    }

    private MissionRequest validRequest() {
        MissionRequest request = new MissionRequest();
        request.setChauffeurId(2L);
        request.setDepart("Alger");
        request.setDestination("Oran");
        request.setDateDebut(LocalDate.of(2026, 6, 8));
        request.setDateFin(LocalDate.of(2026, 6, 9));
        return request;
    }
}
