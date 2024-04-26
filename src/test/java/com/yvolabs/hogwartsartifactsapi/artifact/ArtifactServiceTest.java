package com.yvolabs.hogwartsartifactsapi.artifact;

import com.yvolabs.hogwartsartifactsapi.utils.IdWorker;
import com.yvolabs.hogwartsartifactsapi.wizard.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


/**
 * @author Yvonne N
 */
@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {
    // ! @Mock creates a mock.
    // ! @InjectMocks creates an instance of the class and injects the mocks that are created with the @Mock (or @Spy) annotations into this instance.
    // ! TDD: Red - Green - Refactor
    // ! TDD STEPS: GIVEN -> WHEN -> THEN

    @Mock
    private ArtifactRepository artifactRepository;

    @Mock
    private IdWorker idWorker;

    @InjectMocks
    private ArtifactServiceImpl artifactService;

    List<Artifact> artifacts;


    @BeforeEach
    void setUp() {
        setArtifactsTestData();
    }


    @Test
    void testFindByIdSuccess() {
        // Given. Arrange inputs and targets. Define the behavior of Mock object artifactRepository.
        String artifactId = "1250808601744904192";
        Wizard wizard = Wizard.builder()
                .id(2)
                .name("Harry Potter")
                .build();

        Artifact artifact = Artifact.builder()
                .id(artifactId)
                .name("Invisibility Cloak")
                .description("An invisibility cloak is used to make the wearer invisible.")
                .imageUrl("ImageUrl")
                .owner(wizard)
                .build();

        given(artifactRepository.findById(artifactId)).willReturn(Optional.of(artifact));

        // When. Act on the target behavior. When steps should cover the method to be tested.
        Artifact returnedArtifact = artifactService.findById(artifactId);

        // Then. Assert expected outcomes.
        assertThat(returnedArtifact.getId()).isEqualTo(artifactId);
        assertThat(returnedArtifact.getName()).isEqualTo(artifact.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(artifact.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(artifact.getImageUrl());
        assertThat(returnedArtifact.getOwner()).isEqualTo(wizard);

        verify(artifactRepository, times(1)).findById(artifactId);
    }

    @Test
    void testFindByIdNotFound() {
        given(artifactRepository.findById(Mockito.anyString())).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() ->
                artifactService.findById("1250808601744904192")
        );

        assertThat(throwable)
                .isInstanceOf(ArtifactNotFoundException.class)
                .hasMessageContaining("Could not find artifact with Id 1250808601744904192");

        verify(artifactRepository, times(1)).findById(Mockito.anyString());

    }

    @Test
    void testFindAllSuccess() {
        given(artifactRepository.findAll()).willReturn(artifacts);
        List<Artifact> returnedArtifacts = artifactService.findAll();

        assertThat(returnedArtifacts.size()).isEqualTo(artifacts.size());
        assertThat(returnedArtifacts).containsAll(artifacts);
        assertThat(returnedArtifacts).hasSameElementsAs(artifacts);
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        Artifact newArtifact = Artifact.builder()
                .name("some name")
                .description("some_description")
                .imageUrl("some_image_url")
                .build();
        given(idWorker.nextId()).willReturn(123456L);
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

        Artifact savedArtifact = artifactService.save(newArtifact);

        assertThat(savedArtifact.getId()).isEqualTo(newArtifact.getId());
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());

        verify(artifactRepository, times(1)).save(newArtifact);

    }

    @Test
    void testUpdateSuccess() {
        String artifactId = "1250808601744904191";
        Artifact oldArtifact = artifacts.get(0);
        Artifact update = Artifact.builder()
                .name("updated_name")
                .description("updated_description")
                .imageUrl("updated_imageUrl").build();
        given(artifactRepository.findById(artifactId)).willReturn(Optional.of(oldArtifact));
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        Artifact updatedArtifact = artifactService.update(artifactId, update);

        assertThat(updatedArtifact.getId()).isEqualTo(artifactId);
        assertThat(updatedArtifact.getName()).isEqualTo(update.getName());
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
        assertThat(updatedArtifact.getImageUrl()).isEqualTo(update.getImageUrl());

        verify(artifactRepository, times(1)).findById(artifactId);
        verify(artifactRepository, times(1)).save(oldArtifact);
    }

    @Test
    void testUpdateNotFound() {
        String artifactId = "1250808601744904191";
        Artifact update = Artifact.builder()
                .name("updated_name")
                .description("updated_description")
                .imageUrl("updated_imageUrl").build();

        given(artifactRepository.findById(artifactId)).willReturn(Optional.empty());

        assertThrows(ArtifactNotFoundException.class, () -> artifactService.update(artifactId, update));
        verify(artifactRepository, times(1)).findById(artifactId);
    }

    @Test
    void testDeleteSuccess() {
        String artifactId = "1250808601744904191";
        given(artifactRepository.findById(artifactId)).willReturn(Optional.of(artifacts.get(0)));
        doNothing().when(artifactRepository).deleteById(artifactId);
        artifactService.delete(artifactId);
        verify(artifactRepository, times(1)).deleteById(artifactId);
    }

    @Test
    void testDeleteNotFound() {
        String artifactId = "1250808601744904191";
        given(artifactRepository.findById(artifactId)).willReturn(Optional.empty());
        assertThrows(ArtifactNotFoundException.class, () -> artifactService.delete(artifactId));
        verify(artifactRepository, times(1)).findById(artifactId);
    }

    //helpers
    private void setArtifactsTestData() {
        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("imageUrl");

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("imageUrl");

        this.artifacts = new ArrayList<>();
        this.artifacts.add(a1);
        this.artifacts.add(a2);
    }


}