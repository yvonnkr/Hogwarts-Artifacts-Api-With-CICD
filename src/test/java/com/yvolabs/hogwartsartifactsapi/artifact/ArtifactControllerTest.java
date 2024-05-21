package com.yvolabs.hogwartsartifactsapi.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Yvonne N
 * Typically @WebMvcTest is used in combination with @MockBean or @Import to create any collaborators required by your @Controller beans.
 * If you are looking to load your full application configuration and use MockMVC,
 * you should consider @SpringBootTest combined with @AutoConfigureMockMvc rather than this annotation.
 *
 * In this case sw
 */
//@WebMvcTest(controllers = ArtifactController.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Turn off Spring Security
@ActiveProfiles(value = "dev")
@Slf4j
class ArtifactControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ArtifactService artifactService;

//    @MockBean
//    MeterRegistry meterRegistry; // not needed if using @SpringBootTest instead of @WebMvcTest

    @Autowired
    ObjectMapper objectMapper;

    List<Artifact> artifacts;

    @Value("${api.endpoint.base-url}/artifacts")
    private String PATH;

    @BeforeEach
    void setUp() {
        setArtifactsData();
    }

    @Test
    void tesFindArtifactByIdSuccess() throws Exception {
        String artifactId = "1250808601744904191";
        given(artifactService.findById(artifactId)).willReturn(artifacts.get(0));

//        Counter mockedCounter = mock(Counter.class);
//        given(meterRegistry.counter(any())).willReturn(mockedCounter);

        mockMvc.perform(get(PATH + "/" + artifactId).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Deluminator"));
    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {
        String artifactId = "1250808601744904191";
        given(artifactService.findById(artifactId)).willThrow(new ObjectNotFoundException("artifact", artifactId));

        // When and then
        mockMvc.perform(get(PATH + "/" + artifactId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904191"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {
        given(artifactService.findAll()).willReturn(artifacts);

        mockMvc.perform(get(PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data[0].name").value("Deluminator"))
                .andExpect(jsonPath("$.data[1].id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data[1].name").value("Invisibility Cloak"));
    }

    @Test
    void testAddArtifactSuccess() throws Exception {
        ArtifactDto artifactDto = ArtifactDto.builder()
                .name("some_name")
                .description("some_description")
                .imageUrl("some_image_url")
                .build();

        String artifactDtoJson = objectMapper.writeValueAsString(artifactDto);
        log.info("artifactDtoJson: {}", artifactDtoJson);


        Artifact savedArtifact = Artifact.builder()
                .id("12345")
                .name(artifactDto.name())
                .description(artifactDto.description())
                .imageUrl(artifactDto.imageUrl())
                .build();


        given(artifactService.save(Mockito.any(Artifact.class))).willReturn(savedArtifact);

        this.mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artifactDtoJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));

    }

    @Test
    void testAddArtifactValidation() throws Exception {
        ArtifactDto invalidArtifactDto = ArtifactDto.builder()
                .name("")
                .description("")
                .imageUrl("")
                .build();

        String artifactDtoJson = objectMapper.writeValueAsString(invalidArtifactDto);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artifactDtoJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required."))
                .andExpect(jsonPath("$.data.description").value("description is required."))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl is required."));

    }

    @Test
    void testUpdateArtifactSuccess() throws Exception {
        String artifactId = "1250808601744904191";

        ArtifactDto updateDto = ArtifactDto.builder()
                .name("updated_name")
                .description("updated_description")
                .imageUrl("updated_image").build();

        String updateDtoJson = objectMapper.writeValueAsString(updateDto);

        Artifact updatedArtifact = Artifact.builder()
                .name("updated_name")
                .description("updated_description")
                .imageUrl("updated_image").build();

        given(artifactService.update(eq(artifactId), Mockito.any(Artifact.class))).willReturn(updatedArtifact);

        mockMvc.perform(
                        put(PATH + "/" + artifactId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateDtoJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(updatedArtifact.getId()))
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));

        verify(artifactService).update(eq(artifactId), Mockito.any(Artifact.class));

    }

    @Test
    void testUpdateArtifactErrorWithNonExistentId() throws Exception {
        String artifactId = "1250808601744904192";
        // Given
        ArtifactDto artifactDto = ArtifactDto.builder()
                .id(artifactId)
                .name("Invisibility Cloak")
                .description("A new description.")
                .imageUrl("imageUrl").build();

        String json = this.objectMapper.writeValueAsString(artifactDto);

        given(this.artifactService.update(eq(artifactId), Mockito.any(Artifact.class))).willThrow(new ObjectNotFoundException("artifact", artifactId));

        this.mockMvc.perform(put(PATH + "/" + artifactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id " + artifactId))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateArtifactValidation() throws Exception {
        String artifactId = "1250808601744904191";

        ArtifactDto invalidArtifactDto = ArtifactDto.builder()
                .name("")
                .description("")
                .imageUrl("")
                .build();

        String updateDtoJson
                = objectMapper.writeValueAsString(invalidArtifactDto);

        ResultActions resultActions = mockMvc.perform(
                put(PATH + "/" + artifactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDtoJson)
                        .accept(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required."))
                .andExpect(jsonPath("$.data.description").value("description is required."))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl is required."));

        //verify artifactService will not be called as MethodArgumentNotValidException is expected to be thrown when invalid request body,
        verify(artifactService, times(0)).update(eq(artifactId), Mockito.any(Artifact.class));
    }

    @Test
    void testDeleteArtifactSuccess() throws Exception {
        String artifactId = "1250808601744904191";
        doNothing().when(artifactService).delete(eq(artifactId));

        mockMvc.perform(delete(PATH + "/" + artifactId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {
        String artifactId = "1250808601744904191";
        doThrow(new ObjectNotFoundException("artifact", artifactId)).when(artifactService).delete(eq(artifactId));

        mockMvc.perform(delete(PATH + "/" + artifactId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id " + artifactId))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    private void setArtifactsData() {
        this.artifacts = new ArrayList<>();

        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("ImageUrl");
        this.artifacts.add(a1);

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("ImageUrl");
        this.artifacts.add(a2);

        Artifact a3 = new Artifact();
        a3.setId("1250808601744904193");
        a3.setName("Elder Wand");
        a3.setDescription("The Elder Wand, known throughout history as the Deathstick or the Wand of Destiny, is an extremely powerful wand made of elder wood with a core of Thestral tail hair.");
        a3.setImageUrl("ImageUrl");
        this.artifacts.add(a3);

        Artifact a4 = new Artifact();
        a4.setId("1250808601744904194");
        a4.setName("The Marauder's Map");
        a4.setDescription("A magical map of Hogwarts created by Remus Lupin, Peter Pettigrew, Sirius Black, and James Potter while they were students at Hogwarts.");
        a4.setImageUrl("ImageUrl");
        this.artifacts.add(a4);

        Artifact a5 = new Artifact();
        a5.setId("1250808601744904195");
        a5.setName("The Sword Of Gryffindor");
        a5.setDescription("A goblin-made sword adorned with large rubies on the pommel. It was once owned by Godric Gryffindor, one of the medieval founders of Hogwarts.");
        a5.setImageUrl("ImageUrl");
        this.artifacts.add(a5);

        Artifact a6 = new Artifact();
        a6.setId("1250808601744904196");
        a6.setName("Resurrection Stone");
        a6.setDescription("The Resurrection Stone allows the holder to bring back deceased loved ones, in a semi-physical form, and communicate with them.");
        a6.setImageUrl("ImageUrl");
        this.artifacts.add(a6);
    }
}