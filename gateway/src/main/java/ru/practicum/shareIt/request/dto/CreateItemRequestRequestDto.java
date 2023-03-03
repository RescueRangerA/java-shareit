package ru.practicum.shareIt.request.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateItemRequestRequestDto {
    @NotNull
    @NotBlank
    private String description;
}
