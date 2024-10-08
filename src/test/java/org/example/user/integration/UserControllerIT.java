package org.example.user.integration;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.StringUtils;
import org.example.user.model.domain.ApplicationUser;
import org.example.user.model.enums.UserRole;
import org.example.user.model.enums.UserStatus;
import org.example.user.model.request.UserCreateRequest;
import org.example.user.repository.ApplicationUserRepository;
import org.example.user.security.WithAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UserControllerIT extends BaseIT {
    @Autowired
    private ApplicationUserRepository userRepository;


    //Create tests

    @Test
    @WithAuthentication(role = UserRole.ROLE_USER)
    public void testCreate_shouldReturn403WhenRoleIsUser() throws Exception {
        UserCreateRequest bla = createRequest("bla", "12345");
        ResultActions response = mockMvc.perform(post("/api/users")
                .header(HttpHeaders.HOST, "localhost")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bla)) );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithAuthentication(role = UserRole.ROLE_ADMIN)
    public void testCreate_shouldReturn201WhenRequestIsCorrect() throws Exception {
        UserCreateRequest bla = createRequest("bla", "12345");
        ResultActions response = mockMvc.perform(post("/api/users")
                .header(HttpHeaders.HOST, "localhost")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bla)));

        response.andExpect(status().isCreated());
    }

    @Test
    @WithAuthentication(role = UserRole.ROLE_ADMIN)
    public void testCreate_shouldReturnBadRequestWhenValidationFails() throws Exception {
        UserCreateRequest bla = createRequest("bla", "12");
        ResultActions response = mockMvc.perform(post("/api/users")
                .header(HttpHeaders.HOST, "localhost")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bla)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionId", is("MethodArgumentNotValidException")))
                .andExpect(jsonPath("$.message", containsString("password size must be between 4-32")));
    }

    @Test
    @WithAuthentication(role = UserRole.ROLE_ADMIN)
    public void testCreate_shouldReturnBadRequestWhenValidationFails2() throws Exception {
        UserCreateRequest bla = createRequest(null, null);
        ResultActions response = mockMvc.perform(post("/api/users")
                .header(HttpHeaders.HOST, "localhost")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bla)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionId", is("MethodArgumentNotValidException")))
                .andExpect(jsonPath("$.message", containsString("password can not be null!")))
                .andExpect(jsonPath("$.message", containsString("username can not be empty!")));
    }

    //Listing tests


    @WithAuthentication(role = UserRole.ROLE_ADMIN)
    @ParameterizedTest
    @MethodSource("listSearchData")
    public void testListing_shoudReturnCorrectResponse(LinkedMultiValueMap<String, String> params,
                                                       int expectedCount,
                                                       String expectedFirstElement) throws Exception {

        setupEnv();
        ResultActions response = mockMvc.perform(get("/api/users")
                .header(HttpHeaders.HOST, "localhost")
                .contentType(MediaType.APPLICATION_JSON)
                .params(params));
        response.andExpect(status().is(206))
                .andExpect(jsonPath("$.content", hasSize(expectedCount)));
        if (expectedCount > 0) {
            response.andExpect(jsonPath("$.content[*].id", is(notNullValue())))
                    .andExpect(jsonPath("$.content[*].status", is(notNullValue())))
                    .andExpect(jsonPath("$.content[*].username", is(notNullValue())));
        }
        if (StringUtils.isNotEmpty(expectedFirstElement)) {
            response.andExpect(jsonPath("$.content[0].username", is(expectedFirstElement)));
        }
    }

    private void setupEnv() {
        userRepository.saveAll(List.of(createUser("user1", UserStatus.ACTIVE),
                createUser("user2", UserStatus.ACTIVE),
                createUser("user3", UserStatus.DELETED),
                createUser("user4", UserStatus.ACTIVE)));

    }

    private ApplicationUser createUser(String user1, UserStatus userStatus) {
        return new ApplicationUser(null, user1, "bla", "ble", "bla", userStatus, UserRole.ROLE_ADMIN);
    }

    private static Stream<Arguments> listSearchData() {
        return Stream.of(
                arguments(new LinkedMultiValueMap<>(Map.of(
                        "status", List.of("ACTIVE", "DELETED"),
                        "page", Collections.singletonList("0"),
                        "size", Collections.singletonList("25"),
                        "direction", Collections.singletonList("ASC"),
                        "order", Collections.singletonList("ID")
                )), 4, "user1"),
                arguments(new LinkedMultiValueMap<>(Map.of(
                        "status", List.of("ACTIVE"),
                        "page", Collections.singletonList("0"),
                        "size", Collections.singletonList("25"),
                        "direction", Collections.singletonList("ASC"),
                        "order", Collections.singletonList("ID")
                )), 3, "user1"),
                arguments(new LinkedMultiValueMap<>(Map.of(
                        "usernamePrompt", Collections.singletonList("user1"),
                        "status", List.of("ACTIVE", "DELETED"),
                        "page", Collections.singletonList("0"),
                        "size", Collections.singletonList("25"),
                        "direction", Collections.singletonList("ASC"),
                        "order", Collections.singletonList("ID")
                )), 1, "user1"),
                arguments(new LinkedMultiValueMap<>(Map.of(
                        "status", List.of("ACTIVE", "DELETED"),
                        "page", Collections.singletonList("0"),
                        "size", Collections.singletonList("25"),
                        "direction", Collections.singletonList("DESC"),
                        "order", Collections.singletonList("ID")
                )), 4, "user4"),
                arguments(new LinkedMultiValueMap<>(Map.of(
                        "page", Collections.singletonList("0"),
                        "size", Collections.singletonList("25"),
                        "direction", Collections.singletonList("ASC"),
                        "order", Collections.singletonList("ID")
                )), 4, "user1"),
                arguments(new LinkedMultiValueMap<>(Map.of(
                        "page", Collections.singletonList("0"),
                        "usernamePrompt", Collections.singletonList("fsalflasflaslf"),
                        "size", Collections.singletonList("25"),
                        "direction", Collections.singletonList("ASC"),
                        "order", Collections.singletonList("ID")
                )), 0, "")
        );
    }

    public UserCreateRequest createRequest(String username,String password){
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setUsername(username);
        userCreateRequest.setPassword(password);
        userCreateRequest.setRole(UserRole.ROLE_ADMIN);
        userCreateRequest.setEmail("ble");
        userCreateRequest.setFirstname("bla");
        return userCreateRequest;
    }
}
