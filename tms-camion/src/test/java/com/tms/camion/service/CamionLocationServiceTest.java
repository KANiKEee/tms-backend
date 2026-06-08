package com.tms.camion.service;

import com.tms.camion.dto.LocationResponse;
import com.tms.camion.dto.LocationUpdateRequest;
import com.tms.camion.entity.CamionEntity;
import com.tms.camion.entity.CamionLocationEntity;
import com.tms.camion.repository.CamionLocationRepository;
import com.tms.camion.repository.CamionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CamionLocationServiceTest {

    @Mock
    private CamionLocationRepository locationRepository;

    @Mock
    private CamionRepository camionRepository;

    @InjectMocks
    private CamionLocationService locationService;

    @BeforeEach
    void setUpSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("chauffeur.test", null, List.of()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateLocationLinksMissionAndUsesAuthenticatedDriver() {
        LocationUpdateRequest request = new LocationUpdateRequest();
        request.setMissionId(41L);
        request.setCamionId(7L);
        request.setChauffeurId(12L);
        request.setLatitude(36.7538);
        request.setLongitude(3.0588);
        request.setAccuracy(8.0);
        request.setSpeed(42.0);
        request.setHeading(90.0);

        CamionEntity camion = CamionEntity.builder()
                .id(7L)
                .immatricule("00123-116-16")
                .marque("Renault")
                .build();

        when(camionRepository.findById(7L)).thenReturn(Optional.of(camion));
        when(locationRepository.findByCamionId(7L)).thenReturn(Optional.empty());
        when(locationRepository.save(any(CamionLocationEntity.class))).thenAnswer(invocation -> {
            CamionLocationEntity location = invocation.getArgument(0);
            location.setId(1L);
            return location;
        });

        LocationResponse response = locationService.updateLocation(request);

        assertThat(response.getMissionId()).isEqualTo(41L);
        assertThat(response.getCamionImmatricule()).isEqualTo("00123-116-16");
        assertThat(response.getChauffeurNom()).isEqualTo("chauffeur.test");
        assertThat(response.getAccuracy()).isEqualTo(8.0);

        ArgumentCaptor<CamionLocationEntity> captor =
                ArgumentCaptor.forClass(CamionLocationEntity.class);
        verify(locationRepository).save(captor.capture());
        assertThat(captor.getValue().getChauffeurId()).isEqualTo(12L);
        assertThat(captor.getValue().getLatitude()).isEqualTo(36.7538);
    }
}
