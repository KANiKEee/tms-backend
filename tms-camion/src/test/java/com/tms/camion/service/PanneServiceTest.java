package com.tms.camion.service;

import com.tms.camion.dto.PanneRequest;
import com.tms.camion.dto.PanneResponse;
import com.tms.camion.entity.CamionEntity;
import com.tms.camion.entity.PanneEntity;
import com.tms.camion.repository.CamionRepository;
import com.tms.camion.repository.PanneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PanneServiceTest {

    @Mock
    private PanneRepository panneRepository;

    @Mock
    private CamionRepository camionRepository;

    @InjectMocks
    private PanneService panneService;

    @Test
    void declarePanne_waitsForAdminApprovalBeforeBlockingCamion() {
        CamionEntity camion = CamionEntity.builder()
                .id(10L)
                .immatricule("AA-123-BB")
                .marque("Renault")
                .statut("DISPONIBLE")
                .build();
        PanneRequest request = new PanneRequest();
        request.setCamionId(10L);
        request.setChauffeurId(5L);
        request.setChauffeurNom("chauffeur");
        request.setDescription("Probleme moteur");

        when(camionRepository.findById(10L)).thenReturn(Optional.of(camion));
        when(panneRepository.save(any(PanneEntity.class))).thenAnswer(invocation -> {
            PanneEntity panne = invocation.getArgument(0);
            panne.setId(1L);
            return panne;
        });

        PanneResponse response = panneService.declarePanne(request);

        assertThat(response.getStatut()).isEqualTo("DECLAREE");
        assertThat(camion.getStatut()).isEqualTo("DISPONIBLE");
        verify(camionRepository, never()).save(any(CamionEntity.class));
    }

    @Test
    void approvePanne_marksPanneApprovedAndCamionBroken() {
        PanneEntity panne = PanneEntity.builder()
                .id(1L)
                .camionId(10L)
                .statut("DECLAREE")
                .build();
        CamionEntity camion = CamionEntity.builder()
                .id(10L)
                .immatricule("AA-123-BB")
                .marque("Renault")
                .statut("DISPONIBLE")
                .build();

        when(panneRepository.findById(1L)).thenReturn(Optional.of(panne));
        when(camionRepository.findById(10L)).thenReturn(Optional.of(camion));
        when(panneRepository.save(panne)).thenReturn(panne);

        PanneResponse response = panneService.approvePanne(1L);

        assertThat(response.getStatut()).isEqualTo("APPROUVEE");
        assertThat(camion.getStatut()).isEqualTo("EN_PANNE");
        verify(camionRepository).save(camion);
    }

    @Test
    void startIntervention_requiresApprovedPanne() {
        PanneEntity panne = PanneEntity.builder()
                .id(1L)
                .camionId(10L)
                .statut("DECLAREE")
                .build();
        when(panneRepository.findById(1L)).thenReturn(Optional.of(panne));

        assertThatThrownBy(() -> panneService.updateStatut(1L, "EN_COURS"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("approuvee");

        verify(panneRepository, never()).save(any(PanneEntity.class));
    }

    @Test
    void deleteDeclaredPanne_doesNotChangeCamionStatus() {
        PanneEntity panne = PanneEntity.builder()
                .id(1L)
                .camionId(10L)
                .statut("DECLAREE")
                .build();
        when(panneRepository.findById(1L)).thenReturn(Optional.of(panne));

        panneService.deletePanne(1L);

        verify(panneRepository).delete(panne);
        verify(camionRepository, never()).findById(any());
        verify(camionRepository, never()).save(any(CamionEntity.class));
    }
}
