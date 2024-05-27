package com.yvolabs.hogwartsartifactsapi.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Yvonne N
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Artifact API endpoints")
@Tag("Integration")
@ActiveProfiles(value = "dev")
public class ArtifactControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @Value("${api.endpoint.base-url}/artifacts")
    String ARTIFACTS_PATH;

    @Value("${api.endpoint.base-url}/users")
    String USERS_PATH;

    @BeforeEach
    void setUp() throws Exception {
        //login
        ResultActions resultActions = mockMvc.perform(post(USERS_PATH + "/login")
                .with(httpBasic("john", "123456")));

        // get and set token from login result
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(contentAsString);

        this.token = "Bearer " + jsonObject.getJSONObject("data").getString("token");

    }

    @Test
    @DisplayName("Check findAllArtifacts (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
        // Reset H2 database before calling this test case.
    void testFindAllSuccess() throws Exception {
        mockMvc.perform(get(ARTIFACTS_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("Check findAllArtifacts With Paging and Sorting (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindAllSuccessWithPagingAndSorting() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "0");
        queryParams.add("size", "2");
        queryParams.add("sort", "name,asc");

        mockMvc.perform(get(ARTIFACTS_PATH).params(queryParams).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(2))
                .andExpect(jsonPath("$.data.pageable.sort.sorted").value(true));
    }

    @Test
    @DisplayName("Check findAllArtifacts Throws With Invalid Sorting Property values (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindAllThrowsWithInvalidPagingAndSortingProperty() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "0");
        queryParams.add("size", "20");
        queryParams.add("sort", "name,ascxx"); // invalid sort prop name 'ascxx'

        mockMvc.perform(get(ARTIFACTS_PATH).params(queryParams).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Invalid property reference"))
                .andExpect(jsonPath("$.data").value("No property 'ascxx' found for type 'Artifact'"));

    }

    @Test
    @DisplayName("Check findArtifactById (GET)")
    void testFindArtifactByIdSuccess() throws Exception {
        this.mockMvc.perform(get(ARTIFACTS_PATH + "/1250808601744904191").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Deluminator"));
    }

    @Test
    @DisplayName("Check findArtifactById with non-existent id (GET)")
    void testFindArtifactByIdNotFound() throws Exception {
        this.mockMvc.perform(get(ARTIFACTS_PATH + "/1250808601744904199").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check addArtifact with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddArtifactSuccess() throws Exception {
        Artifact a = new Artifact();
        a.setName("Remembrall");
        a.setDescription("A Remembrall was a magical large marble-sized glass ball that contained smoke which turned red when its owner or user had forgotten something. It turned clear once whatever was forgotten was remembered.");
        a.setImageUrl("ImageUrl");

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(post(ARTIFACTS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Remembrall"))
                .andExpect(jsonPath("$.data.description").value("A Remembrall was a magical large marble-sized glass ball that contained smoke which turned red when its owner or user had forgotten something. It turned clear once whatever was forgotten was remembered."))
                .andExpect(jsonPath("$.data.imageUrl").value("ImageUrl"));

        this.mockMvc.perform(get(ARTIFACTS_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(7)));
    }

    @Test
    @DisplayName("Check addArtifact with invalid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddArtifactErrorWithInvalidInput() throws Exception {
        Artifact a = new Artifact();
        a.setName(""); // Name is not provided.
        a.setDescription(""); // Description is not provided.
        a.setImageUrl(""); // ImageUrl is not provided.

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(post(ARTIFACTS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required."))
                .andExpect(jsonPath("$.data.description").value("description is required."))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl is required."));

        this.mockMvc.perform(get(ARTIFACTS_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(6)));
    }


    @Test
    @DisplayName("Check updateArtifact with valid input (PUT)")
    void testUpdateArtifactSuccess() throws Exception {
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Updated artifact name");
        a.setDescription("Updated description");
        a.setImageUrl("Updated imageUrl");

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(put(ARTIFACTS_PATH + "/1250808601744904192").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data.name").value("Updated artifact name"))
                .andExpect(jsonPath("$.data.description").value("Updated description"))
                .andExpect(jsonPath("$.data.imageUrl").value("Updated imageUrl"));
    }

    @Test
    @DisplayName("Check updateArtifact with non-existent id (PUT)")
    void testUpdateArtifactErrorWithNonExistentId() throws Exception {
        Artifact a = new Artifact();
        a.setId("1250808601744904199"); // This id does not exist in the database.
        a.setName("Updated artifact name");
        a.setDescription("Updated description");
        a.setImageUrl("Updated imageUrl");

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(put(ARTIFACTS_PATH + "/1250808601744904199").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check updateArtifact with invalid input (PUT)")
    void testUpdateArtifactErrorWithInvalidInput() throws Exception {
        Artifact a = new Artifact();
        a.setId("1250808601744904191"); // Valid id
        a.setName(""); // Updated name is empty.
        a.setDescription(""); // Updated description is empty.
        a.setImageUrl(""); // Updated imageUrl is empty.

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(put(ARTIFACTS_PATH + "/1250808601744904191").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required."))
                .andExpect(jsonPath("$.data.description").value("description is required."))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl is required."));
        this.mockMvc.perform(get(ARTIFACTS_PATH + "/1250808601744904191").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Deluminator"));
    }

    @Test
    @DisplayName("Check deleteArtifact with valid input (DELETE)")
    void testDeleteArtifactSuccess() throws Exception {
        this.mockMvc.perform(delete(ARTIFACTS_PATH + "/1250808601744904191").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"));
        this.mockMvc.perform(get(ARTIFACTS_PATH + "/1250808601744904191").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904191"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check deleteArtifact with non-existent id (DELETE)")
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {
        this.mockMvc.perform(delete(ARTIFACTS_PATH + "/1250808601744904199").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findArtifactByDescription() throws Exception {
        // searchCriteria
        Map<String, String> searchCriteria = new HashMap<>();
        searchCriteria.put("description", "hogwarts");

        // pagination
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "0");
        queryParams.add("size", "2");
        queryParams.add("sort", "name,asc");

        mockMvc.perform(post(ARTIFACTS_PATH + "/search").params(queryParams)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchCriteria))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Search Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(2)));


    }

    @Test
    void findArtifactByNameAndDescription() throws Exception {
        // searchCriteria
        Map<String, String> searchCriteria = new HashMap<>();
        searchCriteria.put("name", "sword");
        searchCriteria.put("description", "hogwarts");

        // pagination
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "0");
        queryParams.add("size", "2");
        queryParams.add("sort", "name,asc");

        mockMvc.perform(post(ARTIFACTS_PATH + "/search").params(queryParams)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchCriteria))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Search Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(1)));


    }

    @Test
    void findArtifactByIdAndNameAndOwnerName() throws Exception {
        // searchCriteria
        Map<String, String> searchCriteria = new HashMap<>();
        searchCriteria.put("id", "1250808601744904192");
        searchCriteria.put("name", "cloak");
        searchCriteria.put("ownerName", "harry potter");

        // pagination
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "0");
        queryParams.add("size", "2");
        queryParams.add("sort", "name,asc");

        mockMvc.perform(post(ARTIFACTS_PATH + "/search").params(queryParams)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchCriteria))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Search Success"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(1)));

    }


}
