package ru.practicum.shareit.booking.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class ValidatorTimeInPast implements ConstraintValidator<TimeInPast, LocalDateTime> {
    private final LocalDateTime now = LocalDateTime.now();

    @Override
    public boolean isValid(LocalDateTime time, ConstraintValidatorContext cxt) {
        if (time == null) {
            return false;
        }
        return time.isAfter(now);
    }
}
