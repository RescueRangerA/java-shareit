package ru.practicum.shareit.extension;

import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ToString
public class CustomPageableParameters {
    private final Long offset;

    private final Integer size;

    protected CustomPageableParameters(Long offset, Integer size) {
        this.offset = offset;
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }

    public Long getOffset() {
        return offset;
    }

    public static CustomPageableParameters empty() {
        return new CustomPageableParameters(null, null);
    }

    public static CustomPageableParameters of(Long offset, Integer size) {
        return new CustomPageableParameters(offset, size);
    }

    public Boolean isCompleted() {
        return this.offset != null && this.size != null;
    }

    public Pageable toPageable() {
        return this.isCompleted()
                ? ExtendedPageRequest.ofOffset(this.offset, this.size)
                : Pageable.unpaged();
    }

    public Pageable toPageable(Sort sort) {
        return this.isCompleted()
                ? ExtendedPageRequest.ofOffset(this.offset, this.size, sort)
                : Pageable.unpaged();
    }
}

