package com.yvolabs.hogwartsartifactsapi.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.ChatClient;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.ChatRequest;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.ChatResponse;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.Choice;
import com.yvolabs.hogwartsartifactsapi.client.ai.chat.dto.Message;
import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import com.yvolabs.hogwartsartifactsapi.utils.IdWorker;
import com.yvolabs.hogwartsartifactsapi.wizard.Wizard;
import com.yvolabs.hogwartsartifactsapi.wizard.dto.WizardDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

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
@ActiveProfiles(value = "dev")
class ArtifactServiceImplTest {
    // ! @Mock creates a mock.
    // ! @InjectMocks creates an instance of the class and injects the mocks that are created with the @Mock (or @Spy) annotations into this instance.
    // ! TDD: Red - Green - Refactor
    // ! TDD STEPS: GIVEN -> WHEN -> THEN

    @Mock
    private ArtifactRepository artifactRepository;

    @Mock
    private ChatClient chatClient;

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
                .isInstanceOf(ObjectNotFoundException.class)
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

        assertThrows(ObjectNotFoundException.class, () -> artifactService.update(artifactId, update));
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
        assertThrows(ObjectNotFoundException.class, () -> artifactService.delete(artifactId));
        verify(artifactRepository, times(1)).findById(artifactId);
    }

    @Test
    void testSummarizeSuccess() throws JsonProcessingException {
        // Given:
        WizardDto wizardDto = WizardDto.builder()
                .id(1)
                .name("Albus Dombledore")
                .numberOfArtifacts(2)
                .build();

        List<ArtifactDto> artifactDtos = List.of(
                ArtifactDto.builder()
                        .id("1250808601744904191")
                        .name("Deluminator")
                        .description("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.")
                        .imageUrl("imageUrl")
                        .owner(wizardDto)
                        .build(),
                ArtifactDto.builder()
                        .id("1250808601744904193")
                        .name("Elder Wand")
                        .description("The Elder Wand, known throughout history as the Deathstick or the Wand of Destiny, is an extremely powerful wand made of elder wood with a core of Thestral tail hair.")
                        .imageUrl("imageUrl")
                        .owner(wizardDto)
                        .build()
        );


        // convert artifactDtos to json
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(artifactDtos);

        // prepare messages
        List<Message> messages = List.of(
                Message.builder()
                        .role("system")
                        .content("Your task is to generate a short summary of a given JSON array in at most 100 words. The summary must include the number of artifacts, each artifact's description, and the ownership information. Don't mention that the summary is from a given JSON array.") //todo change to real content
                        .build(),
                Message.builder()
                        .role("user")
                        .content(jsonArray)
                        .build()
        );

        //chatClient to generate a text summary based on the given chatRequest
        ChatRequest chatRequest = ChatRequest.builder()
                .model("gpt-4")
                .messages(messages)
                .build();

        ChatResponse chatResponse = new ChatResponse(List.of(
                new Choice(0, new Message("assistant", "A summary of two artifacts owned by Albus Dumbledore."))));

        given(this.chatClient.generate(chatRequest)).willReturn(chatResponse);

        // When:
        String summary = this.artifactService.summarize(artifactDtos);

        // Then:
        assertThat(summary).isEqualTo("A summary of two artifacts owned by Albus Dumbledore.");
        verify(this.chatClient, times(1)).generate(chatRequest);
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