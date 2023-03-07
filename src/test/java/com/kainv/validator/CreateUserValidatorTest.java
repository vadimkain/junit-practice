package com.kainv.validator;

import com.kainv.dto.CreateUserDto;
import com.kainv.entity.Gender;
import com.kainv.entity.Role;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateUserValidatorTest {
    private final CreateUserValidator validator = CreateUserValidator.getInstance();

    @Test
    void shouldPassValidation() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidBirthday() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01 12:23")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actualResult = validator.validate(dto);

//        Ровно одна ошибка потому что все остальные поля правильные
        assertThat(actualResult.getErrors()).hasSize(1);
//        Проверяем какая именно ошибка, т.е. что это ошибка именно связанная с др
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.birthday");
    }

    @Test
    void invalidGender() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role(Role.USER.name())
                .gender("fake")
                .build();

        ValidationResult actualResult = validator.validate(dto);

//        Ровно одна ошибка потому что все остальные поля правильные
        assertThat(actualResult.getErrors()).hasSize(1);
//        Проверяем какая именно ошибка, т.е. что это ошибка именно связанная с др
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.gender");
    }

    @Test
    void invalidRole() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role("fake")
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actualResult = validator.validate(dto);

//        Ровно одна ошибка потому что все остальные поля правильные
        assertThat(actualResult.getErrors()).hasSize(1);
//        Проверяем какая именно ошибка, т.е. что это ошибка именно связанная с др
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.role");
    }

    @Test
    void invalidRoleGenderBirthday() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("01-01-200")
                .role("fake_role")
                .gender("fake_gender")
                .build();

        ValidationResult actualResult = validator.validate(dto);

//        Ожидаем три ошибки
        assertThat(actualResult.getErrors()).hasSize(3);
//        Получаем все ошибки
        List<String> errorCodes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();
//        В contains() в любом порядке перечисляем ожидаемые значения
        assertThat(errorCodes).contains("invalid.role", "invalid.gender", "invalid.birthday");
    }

}