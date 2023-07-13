package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.exception.StateException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidatorState implements ConstraintValidator<StateValid, String> {

    @Override
    public boolean isValid(String stateInString, ConstraintValidatorContext cxt) {
        try {
            State state = State.valueOf(stateInString);
        } catch (IllegalArgumentException e) {
            throw new StateException(stateInString);
        }
        return true;
    }
}
