package com.yvolabs.hogwartsartifactsapi.wizard;

import com.yvolabs.hogwartsartifactsapi.artifact.Artifact;
import com.yvolabs.hogwartsartifactsapi.artifact.ArtifactRepository;
import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author Yvonne N
 */
@ExtendWith(MockitoExtension.class)
class WizardServiceTest {
    @Mock
    private WizardRepository wizardRepository;

    @Mock
    private ArtifactRepository artifactRepository;

    @InjectMocks
    private WizardServiceImpl wizardService;

    private List<Wizard> wizards;

    @BeforeEach
    void setUp() {
        setWizardsTestData();
    }

    @Test
    void findAll() {
        given(wizardRepository.findAll()).willReturn(wizards);

        List<Wizard> result = wizardService.findAll();

        assertNotNull(result);
        assertEquals(result.size(), wizards.size());
        assertEquals(result.get(0).getName(), wizards.get(0).getName());
        verify(wizardRepository).findAll();
    }

    @Test
    void testFindByIdSuccess() {
        int wizardId = 1;
        given(wizardRepository.findById(wizardId)).willReturn(Optional.ofNullable(wizards.get(0)));

        Wizard result = wizardService.findById(wizardId);

        assertNotNull(result);
        assertEquals(result.getId(), wizards.get(0).getId());
        assertEquals(result.getName(), wizards.get(0).getName());
        verify(wizardRepository).findById(wizardId);
    }

    @Test
    void testFindByIdNotFound() {
        given(wizardRepository.findById(1)).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> wizardService.findById(1));

        assertNotNull(throwable);
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find wizard with Id 1");
    }

    @Test
    void testSaveSuccess() {
        Wizard wizard = Wizard.builder()
                .name("some wizard")
                .build();

        given(wizardRepository.save(wizard)).willReturn(wizard);

        Wizard result = wizardService.save(wizard);

        assertNotNull(result);
        assertEquals(result.getName(), wizard.getName());
        verify(wizardRepository).save(wizard);
    }

    @Test
    void testUpdateSuccess() {
        int wizardId = 1;
        Wizard foundWizard = wizards.get(0);
        Wizard update = Wizard.builder()
                .name("updated name")
                .build();


        given(wizardRepository.findById(wizardId)).willReturn(Optional.of(foundWizard));
        given(wizardRepository.save(foundWizard)).willReturn(foundWizard);
        Wizard result = wizardService.update(wizardId, update);

        assertNotNull(result);
        assertEquals(result.getId(), foundWizard.getId());
        assertEquals(result.getName(), update.getName());
        verify(wizardRepository).findById(wizardId);
        verify(wizardRepository).save(foundWizard);
    }

    @Test
    void testUpdateNotFound() {
        int wizardId = 1;
        Wizard update = Wizard.builder()
                .name("updated name")
                .build();
        given(wizardRepository.findById(wizardId)).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> wizardService.update(wizardId, update));

        assertNotNull(throwable);
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find wizard with Id 1");
    }

    @Test
    void testDeleteSuccess() {
        int wizardId = 1;
        given(wizardRepository.findById(wizardId)).willReturn(Optional.of(wizards.get(0)));
        doNothing().when(wizardRepository).deleteById(wizardId);

        wizardService.delete(wizardId);

        verify(wizardRepository).deleteById(wizardId);
    }

    @Test
    void testDeleteNotFound() {
        int wizardId = 1;
        given(wizardRepository.findById(wizardId)).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> wizardService.delete(wizardId));

        assertNotNull(throwable);
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find wizard with Id 1");

        verify(wizardRepository, times(0)).deleteById(wizardId);
    }

    @Test
    void testAssignArtifactSuccess() {
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        w2.addArtifact(a);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.of(w3));

        this.wizardService.assignArtifact(3, "1250808601744904192");

        assertThat(a.getOwner().getId()).isEqualTo(3);
        assertThat(w3.getArtifacts()).contains(a);
    }


    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() {
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        w2.addArtifact(a);

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardService.assignArtifact(3, "1250808601744904192");
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 3");
        assertThat(a.getOwner().getId()).isEqualTo(2);
    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() {

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardService.assignArtifact(3, "1250808601744904192");
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 1250808601744904192");
    }


    private void setWizardsTestData() {
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

        Wizard wizard1 = Wizard.builder()
                .id(1)
                .name("Albus Dumbledore")
                .artifacts(List.of(a1, a2))
                .build();
        Wizard wizard2 = Wizard.builder()
                .id(2)
                .name("Harry Potter")
                .artifacts(List.of(a1, a2))
                .build();
        Wizard wizard3 = Wizard.builder()
                .id(3)
                .name("Neville Longbottom")
                .artifacts(List.of(a1))
                .build();

        wizards = new ArrayList<>();
        wizards.add(wizard1);
        wizards.add(wizard2);
        wizards.add(wizard3);
    }

}