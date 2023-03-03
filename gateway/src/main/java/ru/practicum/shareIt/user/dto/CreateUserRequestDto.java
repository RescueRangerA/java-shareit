package ru.practicum.shareIt.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareIt.validation.email.ExtendedEmailValidator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDto {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @ExtendedEmailValidator
    private String email;
}
