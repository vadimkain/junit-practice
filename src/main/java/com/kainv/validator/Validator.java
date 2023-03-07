package com.kainv.validator;

public interface Validator<T> {

    ValidationResult validate(T object);
}
