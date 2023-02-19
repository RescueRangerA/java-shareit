package ru.practicum.shareit.config;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.dto.SearchBookingStatus;

public class StringToEnumConverter implements Converter<String, SearchBookingStatus> {
    @Override
    public SearchBookingStatus convert(String source) {
        return SearchBookingStatus.valueOf(source.toUpperCase());
    }
}
