package ru.practicum.shareIt.validation.compareDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompareDateValidatorTest {

    @Setter
    @Getter
    @NoArgsConstructor
    static class DummyDto {
        private LocalDateTime before;

        private LocalDateTime after;
    }

    private CompareDateValidator validator;

    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        CompareDate compareDate = mock(CompareDate.class);
        when(compareDate.before()).thenReturn("before");
        when(compareDate.after()).thenReturn("after");

        validator = new CompareDateValidator();
        validator.initialize(compareDate);

        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_whenObjDoesNotHaveSuchFields_thenPrintStackTraceAndReturnFalse() {
        Boolean result = validator.isValid(new Object(), context);
        assertThat(result, equalTo(false));
    }

    @Test
    void isValid_whenBeforeIsNull_thenReturnFalse() {
        DummyDto dummyDto = new DummyDto();
        dummyDto.setBefore(null);
        dummyDto.setAfter(LocalDateTime.now().plusDays(2));

        Boolean result = validator.isValid(dummyDto, context);
        assertThat(result, equalTo(false));
    }

    @Test
    void isValid_whenAfterIsNull_thenReturnFalse() {
        DummyDto dummyDto = new DummyDto();
        dummyDto.setBefore(LocalDateTime.now().plusDays(1));
        dummyDto.setAfter(null);

        Boolean result = validator.isValid(dummyDto, context);
        assertThat(result, equalTo(false));
    }

    @Test
    void isValid_whenAfterIsBeforeBeforeField_thenReturnFalse() {
        DummyDto dummyDto = new DummyDto();
        dummyDto.setBefore(LocalDateTime.now().plusDays(2));
        dummyDto.setAfter(LocalDateTime.now().plusDays(1));

        Boolean result = validator.isValid(dummyDto, context);
        assertThat(result, equalTo(false));
    }

    @Test
    void isValid_whenBeforeIsBeforeAfter_thenReturnTrue() {
        DummyDto dummyDto = new DummyDto();
        dummyDto.setBefore(LocalDateTime.now().plusDays(1));
        dummyDto.setAfter(LocalDateTime.now().plusDays(2));

        Boolean result = validator.isValid(dummyDto, context);
        assertThat(result, equalTo(true));
    }
}