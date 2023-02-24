package ru.practicum.shareit.extension;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ExtendedPageRequest extends PageRequest {
    private Long offset;

    protected ExtendedPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
        offset = null;
    }

    protected ExtendedPageRequest setOffset(Long offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be less than zero");
        }

        this.offset = offset;

        return this;
    }

    @Override
    public long getOffset() {
        return offset != null ? offset : super.getOffset();
    }

    public static ExtendedPageRequest ofOffset(long offset, int size) {
        return new ExtendedPageRequest(0, size, Sort.unsorted()).setOffset(offset);
    }

    public static ExtendedPageRequest ofOffset(long offset, int size, Sort sort) {
        return new ExtendedPageRequest(0, size, sort).setOffset(offset);
    }
}
