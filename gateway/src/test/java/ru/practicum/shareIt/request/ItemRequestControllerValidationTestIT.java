package ru.practicum.shareIt.request;

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
import ru.practicum.shareIt.request.dto.CreateItemRequestRequestDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemRequestControllerValidationTestIT {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient; // do not remove!

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(get("/").header("X-Sharer-User-Id", 1L))
                .build();
    }

    @SneakyThrows
    @Test
    void findById_whenIdIsNegative_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/{requestId}", "-1")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findById_whenIdIsZero_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/{requestId}", "0")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findById_whenIdIsValid_thenExpectOkStatus() {
        mockMvc.perform(get("/requests/{requestId}", "1")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenFromIsNegative_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/all").param("from", "-1")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenFromIsZero_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/all").param("from", "0")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenFromIsValid_thenExpectOkStatus() {
        mockMvc.perform(get("/requests/all").param("from", "1")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenSizeIsNegative_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/all").param("size", "-1")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenSizeIsZero_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/all").param("size", "0")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllForOtherUsers_whenSizeIsValid_thenExpectOkStatus() {
        mockMvc.perform(get("/requests/all").param("size", "1")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void create_whenDescriptionIsBlank_thenReturnBadRequest() {
        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto(
                ""
        );

        mockMvc.perform(
                        post("/requests")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createItemRequestRequestDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenValid_thenReturnOk() {
        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto(
                "desc"
        );

        mockMvc.perform(
                        post("/requests")
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