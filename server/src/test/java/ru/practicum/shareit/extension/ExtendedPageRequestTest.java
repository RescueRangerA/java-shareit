package ru.practicum.shareit.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExtendedPageRequestTest {

    @Test
    void setOffset_whenOffsetIsNegative_thenThrowException() {
        ExtendedPageRequest extendedPageRequest = new ExtendedPageRequest(0, 1, Sort.unsorted());
        assertThrows(
                IllegalArgumentException.class,
                () -> extendedPageRequest.setOffset(-1L)
        );
    }

    @Test
    void setOffset_whenOffsetIsZero_thenDoNotThrowException() {
        ExtendedPageRequest extendedPageRequest = new ExtendedPageRequest(0, 1, Sort.unsorted());
        assertDoesNotThrow(
                () -> extendedPageRequest.setOffset(0L)
        );
    }

    @Test
    void setOffset_whenOffsetIsPositive_thenDoNotThrowException() {
        ExtendedPageRequest extendedPageRequest = new ExtendedPageRequest(0, 1, Sort.unsorted());
        assertDoesNotThrow(
                () -> extendedPageRequest.setOffset(1L)
        );
    }

    @Test
    void getOffset_whenSetterWasNotInvoked_thenReturnParentResult() {
        ExtendedPageRequest extendedPageRequest = new ExtendedPageRequest(10, 5, Sort.unsorted());

        assertEquals(10L * 5L, extendedPageRequest.getOffset());
    }

    @Test
    void getOffset_whenSetterWasInvoked_thenReturnPreviouslySet() {
        Long offset = 5L;
        ExtendedPageRequest extendedPageRequest = new ExtendedPageRequest(10, 5, Sort.unsorted());
        extendedPageRequest.setOffset(offset);

        assertEquals(offset, extendedPageRequest.getOffset());
    }

    @Test
    void ofOffset() {
        long offset = 5L;
        int size = 2;
        ExtendedPageRequest extendedPageRequest = ExtendedPageRequest.ofOffset(offset, size);

        assertEquals(offset, extendedPageRequest.getOffset());
        assertEquals(size, extendedPageRequest.getPageSize());
        assertEquals(0, extendedPageRequest.getPageNumber());
    }

    @Test
    void ofOffsetSort() {
        long offset = 5L;
        int size = 2;
        Sort sort = Sort.unsorted();
        ExtendedPageRequest extendedPageRequest = ExtendedPageRequest.ofOffset(offset, size, sort);

        assertEquals(offset, extendedPageRequest.getOffset());
        assertEquals(size, extendedPageRequest.getPageSize());
        assertEquals(0, extendedPageRequest.getPageNumber());
        assertEquals(sort, extendedPageRequest.getSort());
    }
}