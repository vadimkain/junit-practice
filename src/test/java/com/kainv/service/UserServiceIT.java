package com.kainv.service;

import com.kainv.dao.UserDao;
import com.kainv.dto.CreateUserDto;
import com.kainv.dto.UserDto;
import com.kainv.entity.Gender;
import com.kainv.entity.Role;
import com.kainv.entity.User;
import com.kainv.integration.IntegrationTestBase;
import com.kainv.mapper.CreateUserMapper;
import com.kainv.mapper.UserMapper;
import com.kainv.validator.CreateUserValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceIT extends IntegrationTestBase {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void init() {
        userDao = UserDao.getInstance();
        userService = new UserService(
                CreateUserValidator.getInstance(),
                userDao,
                CreateUserMapper.getInstance(),
                UserMapper.getInstance()
        );
    }

    @Test
    void login() {
        User user = userDao.save(getUser("test@gmail.com"));

//        Проверяем, что метод login() вернёт пользователя, если передадим email и пароль
        Optional<UserDto> actualResult = userService.login(user.getEmail(), user.getPassword());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void create() {
        CreateUserDto createUserDto = getCreateUserDto();

        UserDto actualResult = userService.create(createUserDto);

//        Проверяем, что у нашего пользователя есть ид, т.е. подразумеваем существование пользователя
        assertNotNull(actualResult.getId());
    }

    private CreateUserDto getCreateUserDto() {
        return CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .password("123")
                .birthday("2000-01-01")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();
    }


    private User getUser(String email) {
        return User.builder()
                .name("Ivan")
                .email(email)
                .password("123")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(Role.USER)
                .gender(Gender.MALE)
                .build();
    }

}