package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.item.dto.CreateItemCommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.security.service.ExtendedUserDetailsService;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerSecurityTestIT {

    private static final String HEADER_NAME = "X-Sharer-User-Id";

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private ExtendedUserDetailsService userDetailsService;

    @MockBean
    private ItemController itemController; // Mock the controller, keep it even there are no usages. Needs to shut down the validator.

    @MockBean(name = "mvcValidator")
    private Validator mockValidator; // Mock the validator, keep it even there are no usages. Needs to shut down the validator.

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
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
    void getAllItems_whenHeaderIsMissing_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/items")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllItems_whenHeaderIsCorrupted_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/items").header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllItems_whenUserNotFound_thenReturnNotFound() {
        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/items").header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllItems_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        mockMvc.perform(
                        get("/items").header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findByText_whenHeaderIsMissing_thenReturnBadRequest() {
        String query = "test";

        mockMvc.perform(
                        get("/items/search").param("text", query)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findByText_whenHeaderIsCorrupted_thenReturnBadRequest() {
        String query = "test";

        mockMvc.perform(
                        get("/items/search")
                                .header(HEADER_NAME, "header")
                                .param("text", query)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findByTexts_whenUserNotFound_thenReturnNotFound() {
        String query = "test";

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/items/search")
                                .header(HEADER_NAME, "0")
                                .param("text", query)
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findByText_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        String query = "test";

        mockMvc.perform(
                        get("/items/search")
                                .header(HEADER_NAME, "0")
                                .param("text", query)
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getItem_whenHeaderIsMissing_thenReturnBadRequest() {
        Long itemId = 0L;

        mockMvc.perform(
                        get("/items/{itemId}", itemId)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getItem_whenHeaderIsCorrupted_thenReturnBadRequest() {
        Long itemId = 0L;

        mockMvc.perform(
                        get("/items/{itemId}", itemId).header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getItem_whenUserNotFound_thenReturnNotFound() {
        Long itemId = 0L;

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/items/{itemId}", itemId).header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getItem_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        Long itemId = 0L;

        mockMvc.perform(
                        get("/items/{itemId}", itemId).header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void createItem_whenHeaderIsMissing_thenReturnBadRequest() {
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();

        mockMvc.perform(
                        post("/items")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createItem_whenHeaderIsCorrupted_thenReturnBadRequest() {
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();

        mockMvc.perform(
                        post("/items")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestDto))
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createItem_whenUserNotFound_thenReturnNotFound() {
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        post("/items")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestDto))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createItem_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();

        mockMvc.perform(
                        post("/items")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestDto))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void updateItem_whenHeaderIsMissing_thenReturnBadRequest() {
        Long itemId = 0L;
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();

        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(updateItemRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateItem_whenHeaderIsCorrupted_thenReturnBadRequest() {
        Long itemId = 0L;
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();

        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(updateItemRequestDto))
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateItem_whenUserNotFound_thenReturnNotFound() {
        Long itemId = 0L;
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(updateItemRequestDto))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateItem_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        Long itemId = 0L;
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();

        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(updateItemRequestDto))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void deleteItem_whenHeaderIsMissing_thenReturnBadRequest() {
        Long itemId = 0L;

        mockMvc.perform(
                        delete("/items/{itemId}", itemId)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void deleteItem_whenHeaderIsCorrupted_thenReturnBadRequest() {
        Long itemId = 0L;

        mockMvc.perform(
                        delete("/items/{itemId}", itemId)
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void deleteItem_whenUserNotFound_thenReturnNotFound() {
        Long itemId = 0L;

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        delete("/items/{itemId}", itemId)
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void deleteItem_whenAccessDenied_thenExpectNotFound() {
        Long itemId = 0L;

        doThrow(AccessDeniedException.class).when(itemController).deleteItem(itemId);
        mockMvc.perform(
                        delete("/items/{itemId}", itemId)
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void deleteItem_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        Long itemId = 0L;

        mockMvc.perform(
                        delete("/items/{itemId}", itemId)
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void addComment_whenHeaderIsMissing_thenReturnBadRequest() {
        Long itemId = 0L;
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto();

        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemCommentDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addComment_whenHeaderIsCorrupted_thenReturnBadRequest() {
        Long itemId = 0L;
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto();

        mockMvc.perform(
                        delete("/items/{itemId}", itemId)
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemCommentDto))
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addComment_whenUserNotFound_thenReturnNotFound() {
        Long itemId = 0L;
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto();

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemCommentDto))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addComment_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        Long itemId = 0L;
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto();

        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemCommentDto))
                                .header(HEADER_NAME, "0")
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