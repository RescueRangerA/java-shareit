package ru.practicum.shareIt.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemRequestDto {
    @Nullable
    private String name;

    @Nullable
    private String description;

    @Nullable
    private Boolean available;
}
