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
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerSecurityTestIT {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserController userController; // Mock the controller, keep it even there are no usages. Needs to shut down the validator.

    @MockBean(name = "mvcValidator")
    private Validator mockValidator; // Mock the validator, keep it even there are no usages. Needs to shut down the validator.

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvokedWithoutCredentials_thenExpectedOkStatus() {
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getUser_whenInvokedWithoutCredentials_thenExpectedOkStatus() {
        Long userId = 0L;

        mockMvc.perform(get("/users/{userId}", userId)).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void createUser_whenInvokedWithoutCredentials_thenExpectedOkStatus() {
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto();

        mockMvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(asJsonString(createUserRequestDto))
        ).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void updateUser_whenInvokedWithoutCredentials_thenExpectedOkStatus() {
        Long userId = 0L;
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto();

        mockMvc.perform(
                patch("/users/{userId}", userId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(asJsonString(updateUserRequestDto))
        ).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void deleteUser_whenInvokedWithoutCredentials_thenExpectedOkStatus() {
        Long userId = 0L;

        mockMvc.perform(delete("/users/{userId}", userId)).andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}