package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.request.dto.CreateItemRequestRequestDto;
import ru.practicum.shareit.security.service.ExtendedUserDetailsService;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemRequestControllerSecurityTestIT {

    private static final String HEADER_NAME = "X-Sharer-User-Id";

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private ExtendedUserDetailsService userDetailsService;

    @MockBean
    private ItemRequestController itemRequestController; // Mock the controller, keep it even there are no usages. Needs to shut down the validator.

    @MockBean(name = "mvcValidator")
    private Validator mockValidator; // Mock the validator, keep it even there are no usages. Needs to shut down the validator.

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockUserForSecurity();
    }

    void mockUserForSecurity() {
        Long userId = 0L;
        ExtendedUserDetails currentUserDetails = new AuthenticatedUser(new User(userId, "", ""));
        when(userDetailsService.loadUserById(userId)).thenReturn(currentUserDetails);
    }

    void mockUserToThrowForSecurity() {
        Long userId = 0L;
        doThrow(ExtendedEntityNotFoundException.class).when(userDetailsService).loadUserById(userId);
    }

    @SneakyThrows
    @Test
    void findById_whenHeaderIsMissing_thenReturnBadRequest() {
        Long itemRequestId = 0L;

        mockMvc.perform(
                        get("/requests/{itemId}", itemRequestId)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findById_whenHeaderIsCorrupted_thenReturnBadRequest() {
        Long itemRequestId = 0L;

        mockMvc.perform(
                        get("/requests/{itemId}", itemRequestId).header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findById_whenUserNotFound_thenReturnNotFound() {
        Long itemRequestId = 0L;

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/requests/{itemId}", itemRequestId).header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findById_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        Long itemRequestId = 0L;

        mockMvc.perform(
                        get("/requests/{itemId}", itemRequestId).header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findAllForCurrentUser_whenHeaderIsMissing_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/requests")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllForCurrentUser_whenHeaderIsCorrupted_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/requests").header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllForCurrentUser_whenUserNotFound_thenReturnNotFound() {
        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/requests").header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findAllForCurrentUser_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        mockMvc.perform(
                        get("/requests").header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }


    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenHeaderIsMissing_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/requests/all")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenHeaderIsCorrupted_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/requests/all").header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenUserNotFound_thenReturnNotFound() {
        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/requests/all").header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        mockMvc.perform(
                        get("/requests/all").header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void create_whenHeaderIsMissing_thenReturnBadRequest() {
        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto();

        mockMvc.perform(
                        post("/requests")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenHeaderIsCorrupted_thenReturnBadRequest() {
        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto();

        mockMvc.perform(
                        post("/requests")
                                .header(HEADER_NAME, "header")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenUserNotFound_thenReturnNotFound() {
        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto();

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        post("/requests")
                                .header(HEADER_NAME, "0")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestRequestDto))
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void create_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto();

        mockMvc.perform(
                        post("/requests")
                                .header(HEADER_NAME, "0")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestRequestDto))
                )
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}