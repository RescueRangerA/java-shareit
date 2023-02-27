package ru.practicum.shareIt.booking;

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
import ru.practicum.shareIt.booking.dto.CreateBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerValidationTestIT {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;  // do not remove!

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(get("/").header("X-Sharer-User-Id", 1L))
                .build();
    }

    @SneakyThrows
    @Test
    void create_whenStartDateGreaterThanFinishDateInFuture_thenReturnedBadRequest() {
        CreateBookingDto createBookingDto = new CreateBookingDto(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                1L
        );

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenStartDateIsEqualFinishDateInFuture_thenReturnedBadRequest() {
        LocalDateTime moment = LocalDateTime.now();

        CreateBookingDto createBookingDto = new CreateBookingDto(
                moment,
                moment,
                1L
        );

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenStartDateLessThanFinishDateInFuture_thenReturnedOk() {
        CreateBookingDto createBookingDto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L
        );

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void create_whenStartDateAndFinishDateIsInThePast_thenReturnedBadRequest() {
        CreateBookingDto createBookingDto = new CreateBookingDto(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                1L
        );

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenStartDateIsInThePastAndFinishDateIsInTheFuture_thenReturnedBadRequest() {
        CreateBookingDto createBookingDto = new CreateBookingDto(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                1L
        );

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenItemIdIsNegative_thenReturnBadRequest() {
        CreateBookingDto createBookingDto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                -1L
        );

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenItemIdIsZero_thenReturnBadRequest() {
        CreateBookingDto createBookingDto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                0L
        );

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByStatus_whenProvideUnknownState_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings").param("state", "UNKNOWN_STATE")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllForCurrentUserByStatus_whenProvideUnknownState_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings/owner").param("state", "UNKNOWN_STATE")).andExpect(status().isBadRequest());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().findAndRegisterModules().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}