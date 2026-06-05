package com.tms.camion.service;

import com.tms.camion.dto.CamionRequest;
import com.tms.camion.dto.CamionResponse;
import com.tms.camion.entity.CamionEntity;
import com.tms.camion.repository.CamionRepository;
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
class CamionServiceTest {

    @Mock
    private CamionRepository camionRepository;

    @InjectMocks
    private CamionService camionService;

    @Test
    void createCamion_createsCamionWithDefaultStatus() {
        CamionRequest request = new CamionRequest();
        request.setImmatricule("AA-123-BB");
        request.setMarque("Renault");
        request.setModele("T");

        when(camionRepository.existsByImmatricule("AA-123-BB")).thenReturn(false);
        when(camionRepository.save(any(CamionEntity.class))).thenAnswer(invocation -> {
            CamionEntity camion = invocation.getArgument(0);
            camion.setId(1L);
            return camion;
        });

        CamionResponse response = camionService.createCamion(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getImmatricule()).isEqualTo("AA-123-BB");
        assertThat(response.getMarque()).isEqualTo("Renault");
        assertThat(response.getStatut()).isEqualTo("DISPONIBLE");
        verify(camionRepository).save(any(CamionEntity.class));
    }

    @Test
    void createCamion_throwsWhenImmatriculeAlreadyExists() {
        CamionRequest request = new CamionRequest();
        request.setImmatricule("AA-123-BB");

        when(camionRepository.existsByImmatricule("AA-123-BB")).thenReturn(true);

        assertThatThrownBy(() -> camionService.createCamion(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("AA-123-BB");

        verify(camionRepository, never()).save(any(CamionEntity.class));
    }

    @Test
    void updateStatut_throwsWhenCamionDoesNotExist() {
        when(camionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> camionService.updateStatut(99L, "EN_MISSION"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(camionRepository, never()).save(any(CamionEntity.class));
    }
}
