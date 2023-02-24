package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerValidationTestIT {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // do not remove!

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @SneakyThrows
    @Test
    void getUser_whenUserIdIsNegative_thenReturnBadRequest() {
        mockMvc.perform(get("/users/{userId}", "-1")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getUser_whenUserIdIsZero_thenReturnBadRequest() {
        mockMvc.perform(get("/users/{userId}", "0")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getUser_whenUserIdIsValid_thenExpectOkStatus() {
        mockMvc.perform(get("/users/{userId}", "1")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void createUser_whenNameIsBlank_thenReturnBadRequest() {
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto(
                "", "a@a.com"
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createUserRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailIsBlank_thenReturnBadRequest() {
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto(
                "name", ""
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createUserRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailIsIncorrect_thenReturnBadRequest() {
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto(
                "name", "email"
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createUserRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailIsIncorrectNowadays_thenReturnBadRequest() {
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto(
                "name", "a@a"
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createUserRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUser_whenValid_thenReturnOk() {
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto(
                "name", "a@a.com"
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createUserRequestDto))
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIdIsNegative_thenReturnBadRequest() {
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto(
                "name", "a@a.com"
        );

        mockMvc.perform(
                patch("/users/{userId}", "-1")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(asJsonString(updateUserRequestDto))
        ).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIdIsZero_thenReturnBadRequest() {
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto(
                "name", "a@a.com"
        );

        mockMvc.perform(
                patch("/users/{userId}", "0")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(asJsonString(updateUserRequestDto))
        ).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUser_whenEmailIsBlank_thenReturnBadRequest() {
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto(
                "name", ""
        );

        mockMvc.perform(
                patch("/users/{userId}", "1")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(asJsonString(updateUserRequestDto))
        ).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUser_whenEmailIsIncorrectNowadays_thenReturnBadRequest() {
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto(
                "name", "a@a"
        );

        mockMvc.perform(
                patch("/users/{userId}", "1")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(asJsonString(updateUserRequestDto))
        ).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUser_whenInvoke_thenExpectOkStatus() {
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto(
                "name", "a@a.com"
        );

        mockMvc.perform(
                patch("/users/{userId}", "1")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(asJsonString(updateUserRequestDto))
        ).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void deleteUser_whenUserIdIsNegative_thenReturnBadRequest() {
        mockMvc.perform(
                delete("/users/{userId}", "-1")
        ).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void deleteUser_whenUserIdIsZero_thenReturnBadRequest() {
        mockMvc.perform(
                delete("/users/{userId}", "0")
        ).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void deleteUser_whenInvoke_thenExpectOkStatus() {
        mockMvc.perform(
                delete("/users/{userId}", "1")
        ).andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}