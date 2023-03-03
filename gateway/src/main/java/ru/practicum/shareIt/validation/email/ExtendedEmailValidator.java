package ru.practicum.shareIt.validation.email;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * It's important to note that we're rolling our own custom annotation instead
 * of Hibernate's @Email because Hibernate considers the old intranet addresses format,
 * myaddress@myserver, as valid, which isn't good.
 *
 * @see <a href="https://stackoverflow.com/a/4461197">Stackoverflow</a>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = ExtendedEmailValidatorConstants.PATTERN, flags = Pattern.Flag.CASE_INSENSITIVE)
public @interface ExtendedEmailValidator {
    String message() default "{javax.validation.constraints.Email.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
