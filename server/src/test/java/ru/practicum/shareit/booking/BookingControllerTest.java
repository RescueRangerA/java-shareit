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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Captor
    private ArgumentCaptor<CustomPageableParameters> customPageableParametersArgumentCaptor;

    @Test
    void create_whenInvoked_thenReturnedBookingDto() {
        ResponseBookingDto expectedBookingDto = new ResponseBookingDto();
        CreateBookingDto createBookingDto = new CreateBookingDto();

        when(bookingService.create(createBookingDto)).thenReturn(expectedBookingDto);

        ResponseBookingDto actualBookingDto = bookingController.create(createBookingDto);
        verify(bookingService).create(createBookingDto);
        assertThat(actualBookingDto, equalTo(expectedBookingDto));
    }

    @Test
    void updateBookingStatus_whenApprovedIsTrue_thenReturnedBookingDto() {
        Long bookingId = 0L;
        ResponseBookingDto expectedBookingDto = new ResponseBookingDto();

        when(bookingService.updateStatus(bookingId, BookingStatus.APPROVED)).thenReturn(expectedBookingDto);

        ResponseBookingDto actualBookingDto = bookingController.updateBookingStatus(bookingId, true);
        verify(bookingService).updateStatus(bookingId, BookingStatus.APPROVED);
        assertThat(actualBookingDto, equalTo(expectedBookingDto));
    }

    @Test
    void updateBookingStatus_whenApprovedIsFalse_thenReturnedBookingDto() {
        Long bookingId = 0L;
        ResponseBookingDto expectedBookingDto = new ResponseBookingDto();

        when(bookingService.updateStatus(bookingId, BookingStatus.REJECTED)).thenReturn(expectedBookingDto);

        ResponseBookingDto actualBookingDto = bookingController.updateBookingStatus(bookingId, false);
        verify(bookingService).updateStatus(bookingId, BookingStatus.REJECTED);
        assertThat(actualBookingDto, equalTo(expectedBookingDto));
    }

    @Test
    void get_whenInvoked_thenReturnedBookingDto() {
        Long bookingId = 0L;
        ResponseBookingDto expectedBookingDto = new ResponseBookingDto();

        when(bookingService.findOne(bookingId)).thenReturn(expectedBookingDto);

        ResponseBookingDto actualBookingDto = bookingController.get(bookingId);
        verify(bookingService).findOne(bookingId);
        assertThat(actualBookingDto, equalTo(expectedBookingDto));
    }

    @Test
    void getAllByStatus_whenInvoked_thenReturnedBookingCollection() {
        List<ResponseBookingDto> expectedBookingDtos = List.of(new ResponseBookingDto());

        when(
                bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                        any(SearchBookingStatus.class),
                        eq(CustomPageableParameters.of(0L, 10))
                )
        )
                .thenReturn(expectedBookingDtos);

        List<ResponseBookingDto> actualBookingDtos = bookingController.getAllByStatus(SearchBookingStatus.ALL, 0L, 10);
        verify(bookingService).findAllBookedByCurrentUserByStatusOrderByDateDesc(
                any(SearchBookingStatus.class),
                eq(CustomPageableParameters.of(0L, 10))
        );
        assertThat(actualBookingDtos, equalTo(expectedBookingDtos));
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
        verify(bookingService).findAllBookedByCurrentUserByStatusOrderByDateDesc(
                any(SearchBookingStatus.class),
                any(CustomPageableParameters.class)
        );
        assertThat(actualBookingDtos, equalTo(expectedBookingDtos));

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
                        eq(CustomPageableParameters.of(0L, 10))
                )
        )
                .thenReturn(expectedBookingDtos);

        List<ResponseBookingDto> actualBookingDtos = bookingController.getAllForCurrentUserByStatus(
                SearchBookingStatus.ALL,
                0L,
                10
        );
        verify(bookingService).findAllForCurrentUserItemsByStatusOrderByDateDesc(
                any(SearchBookingStatus.class),
                eq(CustomPageableParameters.of(0L, 10))
        );
        assertThat(actualBookingDtos, equalTo(expectedBookingDtos));
    }
}