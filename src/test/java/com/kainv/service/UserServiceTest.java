package com.kainv.service;

import com.kainv.dao.UserDao;
import com.kainv.dto.CreateUserDto;
import com.kainv.dto.UserDto;
import com.kainv.entity.Gender;
import com.kainv.entity.Role;
import com.kainv.entity.User;
import com.kainv.exception.ValidationException;
import com.kainv.integration.IntegrationTestBase;
import com.kainv.mapper.CreateUserMapper;
import com.kainv.mapper.UserMapper;
import com.kainv.validator.CreateUserValidator;
import com.kainv.validator.Error;
import com.kainv.validator.ValidationResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CreateUserValidator createUserValidator;
    @Mock
    private UserDao userDao;
    @Mock
    private CreateUserMapper createUserMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void loginSuccess() {
        User user = getUser();
        UserDto userDto = getUserDto();
        doReturn(Optional.of(user)).when(userDao).findByEmailAndPassword(user.getEmail(), user.getPassword());
        doReturn(userDto).when(userMapper).map(user);

        Optional<UserDto> actualResult = userService.login(user.getEmail(), user.getPassword());

//        Проверяем, что такой объект есть в нашем Optional
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(userDto);
    }

    @Test
    void loginFailed() {
//        Нам все равно какие логины и пароли поэтому пишем any()
        doReturn(Optional.empty()).when(userDao).findByEmailAndPassword(any(), any());

//        TODO: Почему бы тут тоже any() не поставить?
        Optional<UserDto> actualResult = userService.login("dummy", "123");

        assertThat(actualResult).isEmpty();
//        Дальше проверяем, что наш userMapper не был вызван в методе login userService'а, если не было никакого юзера
        verifyNoInteractions(userMapper);
    }

    @Test
    void create() {
        CreateUserDto createUserDto = getCreateUserDto();
        User user = getUser();
        UserDto userDto = getUserDto();
//        Возвращаем пустой ValidationResult когда в createUserValidator проверяем createUserDto
        doReturn(new ValidationResult()).when(createUserValidator).validate(createUserDto);
        doReturn(user).when(createUserMapper).map(createUserDto);
        doReturn(userDto).when(userMapper).map(user);

        UserDto actualResult = userService.create(createUserDto);

        assertThat(actualResult).isEqualTo(userDto);
//        Проверяем, что вызвался метод save(). По логике он и так уже вызван на этапе return userMapper.map(),
//        но со временем логика может измениться и все равно лучше написать verify на userDao.save()
        verify(userDao).save(user);
    }

    @Test
    void shouldThrowExceptionIfDtoInvalid() {
        CreateUserDto createUserDto = getCreateUserDto();
//        Возвращаем ValidationResult с ошибкой
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.role", "message"));
        doReturn(validationResult).when(createUserValidator).validate(createUserDto);

        assertThrows(ValidationException.class, () -> userService.create(createUserDto));
//        Проверяем, что не было никаких взаимодействий с другими объектами в UserService потому что валидация не прошла
//        и следовательно, не mapper'ы, не сохранение в БД не должно было произойти
        verifyNoInteractions(userDao, createUserMapper, userMapper);
    }

    private static CreateUserDto getCreateUserDto() {
        return CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();
    }

    private static UserDto getUserDto() {
        return UserDto.builder()
                .id(99)
                .name("Ivan")
                .email("test@gmail.com")
                .role(Role.USER)
                .birthday(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .build();
    }

    private static User getUser() {
        return User.builder()
                .id(99)
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(Role.USER)
                .gender(Gender.MALE)
                .build();
    }
}