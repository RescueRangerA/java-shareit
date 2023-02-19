package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.SearchBookingStatus;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.extension.CustomPageableParameters;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Captor
    ArgumentCaptor<CustomPageableParameters> customPageableParametersArgumentCaptor;

    @Test
    void create_whenInvoked_thenReturnedBookingDto() {
        ResponseBookingDto expectedBookingDto = new ResponseBookingDto();
        CreateBookingDto createBookingDto = new CreateBookingDto();

        when(bookingService.create(createBookingDto)).thenReturn(expectedBookingDto);

        ResponseBookingDto actualBookingDto = bookingController.create(createBookingDto);
        assertThat(actualBookingDto, hasToString(expectedBookingDto.toString()));
    }

    @Test
    void updateBookingStatus_whenApprovedIsTrue_thenReturnedBookingDto() {
        Long bookingId = 0L;
        ResponseBookingDto expectedBookingDto = new ResponseBookingDto();

        when(bookingService.updateStatus(bookingId, BookingStatus.APPROVED)).thenReturn(expectedBookingDto);

        ResponseBookingDto actualBookingDto = bookingController.updateBookingStatus(bookingId, true);
        assertThat(actualBookingDto, hasToString(expectedBookingDto.toString()));
    }

    @Test
    void updateBookingStatus_whenApprovedIsFalse_thenReturnedBookingDto() {
        Long bookingId = 0L;
        ResponseBookingDto expectedBookingDto = new ResponseBookingDto();

        when(bookingService.updateStatus(bookingId, BookingStatus.REJECTED)).thenReturn(expectedBookingDto);

        ResponseBookingDto actualBookingDto = bookingController.updateBookingStatus(bookingId, false);
        assertThat(actualBookingDto, hasToString(expectedBookingDto.toString()));
    }

    @Test
    void get_whenInvoked_thenReturnedBookingDto() {
        Long bookingId = 0L;
        ResponseBookingDto expectedBookingDto = new ResponseBookingDto();

        when(bookingService.findOne(bookingId)).thenReturn(expectedBookingDto);

        ResponseBookingDto actualBookingDto = bookingController.get(bookingId);
        assertThat(actualBookingDto, hasToString(expectedBookingDto.toString()));
    }

    @Test
    void getAllByStatus_whenInvoked_thenReturnedBookingCollection() {
        List<ResponseBookingDto> expectedBookingDtos = List.of(new ResponseBookingDto());

        when(
                bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                        any(SearchBookingStatus.class),
                        any(CustomPageableParameters.class)
                )
        )
                .thenReturn(expectedBookingDtos);

        List<ResponseBookingDto> actualBookingDtos = bookingController.getAllByStatus(SearchBookingStatus.ALL, null, null);
        assertThat(actualBookingDtos, hasToString(expectedBookingDtos.toString()));
    }

    @Test
    void getAllByStatus_whenInvokedPageable_thenReturnedBookingCollection() {
        List<ResponseBookingDto> expectedBookingDtos = List.of(new ResponseBookingDto());

        when(
                bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                        any(SearchBookingStatus.class),
                        any(CustomPageableParameters.class)
                )
        )
                .thenReturn(expectedBookingDtos);

        List<ResponseBookingDto> actualBookingDtos = bookingController.getAllByStatus(SearchBookingStatus.ALL, 1L, 2);

        assertThat(actualBookingDtos, hasToString(expectedBookingDtos.toString()));

        verify(bookingService).findAllBookedByCurrentUserByStatusOrderByDateDesc(
                any(SearchBookingStatus.class),
                customPageableParametersArgumentCaptor.capture()
        );
        assertThat(customPageableParametersArgumentCaptor.getValue().getOffset(), equalTo(1L));
        assertThat(customPageableParametersArgumentCaptor.getValue().getSize(), equalTo(2));
    }

    @Test
    void getAllForCurrentUserByStatus_whenInvoked_thenReturnedBookingCollection() {
        List<ResponseBookingDto> expectedBookingDtos = List.of(new ResponseBookingDto());

        when(
                bookingService.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                        any(SearchBookingStatus.class),
                        any(CustomPageableParameters.class)
                )
        )
                .thenReturn(expectedBookingDtos);

        List<ResponseBookingDto> actualBookingDtos = bookingController.getAllForCurrentUserByStatus(
                SearchBookingStatus.ALL,
                null,
                null
        );
        assertThat(actualBookingDtos, hasToString(expectedBookingDtos.toString()));
    }
}