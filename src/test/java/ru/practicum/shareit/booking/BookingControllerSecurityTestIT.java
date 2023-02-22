package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.security.service.ExtendedUserDetailsService;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerSecurityTestIT {

    private static final String HEADER_NAME = "X-Sharer-User-Id";

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private ExtendedUserDetailsService userDetailsService;

    @MockBean
    private BookingController bookingController; // Mock the controller, keep it even there are no usages. Needs to shut down the validator.

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
    void create_whenHeaderIsMissing_thenReturnBadRequest() {
        CreateBookingDto createBookingDto = new CreateBookingDto();

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenHeaderIsCorrupted_thenReturnBadRequest() {
        CreateBookingDto createBookingDto = new CreateBookingDto();

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void create_whenUserNotFound_thenReturnNotFound() {
        CreateBookingDto createBookingDto = new CreateBookingDto();

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void create_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        CreateBookingDto createBookingDto = new CreateBookingDto();

        mockMvc.perform(
                        post("/bookings")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(asJsonString(createBookingDto))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void updateBookingStatus_whenHeaderIsMissing_thenReturnBadRequest() {
        Long bookingId = 0L;
        Boolean approved = true;

        mockMvc.perform(
                        patch("/bookings/{bookingId}", bookingId)
                                .param("approved", String.valueOf(approved))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateBookingStatus_whenHeaderIsCorrupted_thenReturnBadRequest() {
        Long bookingId = 0L;
        Boolean approved = true;

        mockMvc.perform(
                        patch("/bookings/{bookingId}", bookingId)
                                .param("approved", String.valueOf(approved))
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateBookingStatus_whenUserNotFound_thenReturnNotFound() {
        Long bookingId = 0L;
        Boolean approved = true;

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        patch("/bookings/{bookingId}", bookingId)
                                .param("approved", String.valueOf(approved))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateBookingStatus_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        Long bookingId = 0L;
        Boolean approved = true;

        mockMvc.perform(
                        patch("/bookings/{bookingId}", bookingId)
                                .param("approved", String.valueOf(approved))
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }


    @SneakyThrows
    @Test
    void get_whenHeaderIsMissing_thenReturnBadRequest() {
        Long bookingId = 0L;

        mockMvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void get_whenHeaderIsCorrupted_thenReturnBadRequest() {
        Long bookingId = 0L;

        mockMvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void get_whenUserNotFound_thenReturnNotFound() {
        Long bookingId = 0L;

        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void get_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        Long bookingId = 0L;

        mockMvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getAllByStatus_whenHeaderIsMissing_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/bookings")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByStatus_whenHeaderIsCorrupted_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/bookings")
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByStatus_whenUserNotFound_thenReturnNotFound() {
        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/bookings")
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllByStatus_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        mockMvc.perform(
                        get("/bookings")
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getAllForCurrentUserByStatus_whenHeaderIsMissing_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/bookings/owner")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllForCurrentUserByStatus_whenHeaderIsCorrupted_thenReturnBadRequest() {
        mockMvc.perform(
                        get("/bookings/owner")
                                .header(HEADER_NAME, "header")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllForCurrentUserByStatus_whenUserNotFound_thenReturnNotFound() {
        mockUserToThrowForSecurity();
        mockMvc.perform(
                        get("/bookings/owner")
                                .header(HEADER_NAME, "0")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllForCurrentUserByStatus_whenSuccessfullyAuthenticated_thenExpectOkStatus() {
        mockMvc.perform(
                        get("/bookings/owner")
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