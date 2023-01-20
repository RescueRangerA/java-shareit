package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.validation.ExtendedEmailValidator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {

    @Nullable
    private String name;

    @Nullable
    @ExtendedEmailValidator
    private String email;
}
