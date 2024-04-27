package com.yvolabs.hogwartsartifactsapi.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.hogwartsartifactsapi.artifact.Artifact;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import com.yvolabs.hogwartsartifactsapi.wizard.dto.WizardDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Yvonne N
 */
@WebMvcTest(controllers = WizardController.class)
class WizardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WizardService wizardService;

    @Autowired
    ObjectMapper objectMapper;

    List<Wizard> wizards;

    @Value("${api.endpoint.base-url}/wizards")
    private String PATH;

    @BeforeEach
    void setUp() {
        setWizardsData();
    }

    @Test
    void testFindAllWizardsSuccess() throws Exception {
        given(wizardService.findAll()).willReturn(wizards);

        mockMvc.perform(get(PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(wizards.size())));
        verify(wizardService).findAll();
    }

    @Test
    void testFindWizardByIdSuccess() throws Exception {
        int wizardId = 1;
        Wizard foundWizard = wizards.get(0);

        given(wizardService.findById(1)).willReturn(foundWizard);

        mockMvc.perform(get(PATH + "/" + wizardId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(wizardId))
                .andExpect(jsonPath("$.data.name").value(foundWizard.getName()));
        verify(wizardService).findById(1);
    }

    @Test
    void testFindWizardErrorWithNonExistentId() throws Exception {
        int wizardId = 1;
        given(wizardService.findById(wizardId)).willThrow(new ObjectNotFoundException("wizard", 1));

        mockMvc.perform(get(PATH + "/" + wizardId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id " + wizardId))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(wizardService).findById(1);
    }

    @Test
    void testAddWizardSuccess() throws Exception {
        WizardDto request = WizardDto.builder()
                .name("new wizard")
                .build();

        Wizard savedWizard = Wizard.builder()
                .id(4)
                .name("new wizard")
                .artifacts(List.of())
                .build();

        given(wizardService.save(any())).willReturn(savedWizard);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.name").value(savedWizard.getName()));
        verify(wizardService).save(any());
    }

    @Test
    void testAddWizardValidation() throws Exception {
        WizardDto request = WizardDto.builder()
                .name("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"));
        verify(wizardService, times(0)).save(any());
    }

    @Test
    void testUpdateWizardSuccess() throws Exception {
        int wizardId = 1;
        WizardDto request = WizardDto.builder()
                .name("updated name")
                .numberOfArtifacts(0)
                .build();

        Wizard updatedWizard = Wizard.builder()
                .id(wizardId)
                .name("updated name")
                .artifacts(List.of())
                .build();


        given(wizardService.update(eq(wizardId), any())).willReturn(updatedWizard);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put(PATH + "/" + wizardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(wizardId))
                .andExpect(jsonPath("$.data.name").value(updatedWizard.getName()));
        verify(wizardService).update(eq(wizardId), any());

    }

    @Test
    void testUpdateWizardValidation() throws Exception {
        int wizardId = 1;
        WizardDto request = WizardDto.builder()
                .name("")
                .numberOfArtifacts(0)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put(PATH + "/" + wizardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"));
        verify(wizardService, times(0)).update(anyInt(), any());

    }

    @Test
    void testUpdateWizardErrorWithNonExistentId() throws Exception {
        int wizardId = 1;

        WizardDto request = WizardDto.builder()
                .name("updated name")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        given(wizardService.update(any(), any())).willThrow(new ObjectNotFoundException("wizard", 1));

        mockMvc.perform(put(PATH + "/" + wizardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1"));

        verify(wizardService, times(1)).update(any(), any());
    }

    @Test
    void testAssignArtifactSuccess() throws Exception {
        doNothing().when(wizardService).assignArtifact(any(), any());

        // wizards/{wizardId}/artifacts/{artifactId}
        mockMvc.perform(put(PATH + "/1/artifacts/1250808601744904192")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"));
        verify(wizardService, times(1)).assignArtifact(any(), any());
    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() throws Exception {
        doThrow(new ObjectNotFoundException("artifact", "123")).when(wizardService).assignArtifact(any(), any());

        mockMvc.perform(put(PATH + "/1/artifacts/1250808601744904192")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 123"));
        verify(wizardService, times(1)).assignArtifact(any(), any());

    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() throws Exception {
        doThrow(new ObjectNotFoundException("wizard", "123")).when(wizardService).assignArtifact(any(), any());

        mockMvc.perform(put(PATH + "/1/artifacts/1250808601744904192")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 123"));
        verify(wizardService, times(1)).assignArtifact(any(), any());

    }

    @Test
    void testDeleteWizardSuccess() throws Exception {
        int wizardId = 1;
        doNothing().when(wizardService).delete(wizardId);

        mockMvc.perform(delete(PATH + "/" + wizardId).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(wizardService).delete(wizardId);
    }

    @Test
    void testDeleteWizardErrorWithNonExistentId() throws Exception {
        int wizardId = 1;
        doThrow(new ObjectNotFoundException("wizard", wizardId)).when(wizardService).delete(wizardId);
        mockMvc.perform(delete(PATH + "/" + wizardId).accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id " + wizardId))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(wizardService).delete(wizardId);
    }

    private void setWizardsData() {
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