package ru.practicum.shareIt.item.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateItemCommentDto {

    @NotNull
    @NotBlank
    private String text;
}
