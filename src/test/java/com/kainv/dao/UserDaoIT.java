package com.kainv.dao;

import com.kainv.entity.Gender;
import com.kainv.entity.Role;
import com.kainv.entity.User;
import com.kainv.integration.IntegrationTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoIT extends IntegrationTestBase {

    private final UserDao userDao = UserDao.getInstance();

    @Test
    void findAll() {
        User user1 = userDao.save(getUser("test1@gmail.com"));
        User user2 = userDao.save(getUser("test2@gmail.com"));
        User user3 = userDao.save(getUser("test3@gmail.com"));

        List<User> actualResult = userDao.findAll();

        assertThat(actualResult).hasSize(3);
//        Если результат равен трём, то должны проверить, что внутри этого списка
//        Проверим, что в результате находятся необходимые нам ид
        List<Integer> userIds = actualResult.stream()
                .map(User::getId)
                .toList();
        assertThat(userIds).contains(user1.getId(), user2.getId(), user3.getId());
    }

    @Test
    void findById() {
        User user = userDao.save(getUser("test1@gmail.com"));

        Optional<User> actualResult = userDao.findById(user.getId());

//        Проверяем, что юзер существует
        assertThat(actualResult).isPresent();
//        Сравниваем полностью объекты
        assertThat(actualResult.get()).isEqualTo(user);

    }

    @Test
    void save() {
        User user = getUser("test1@gmail.com");

        User actualResult = userDao.save(user);

//        Проверяем, есть ли ид у пользователя. Если есть, то пользователь существует.
//        Используем assertNotNull потому что не знаем какой ид будет
        assertNotNull(actualResult.getId());
    }

    @Test
    void findByEmailAndPassword() {
        User user = userDao.save(getUser("test1@gmail.com"));

        Optional<User> actualResult = userDao.findByEmailAndPassword(user.getEmail(), user.getPassword());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(user);
    }

    @Test
    void shouldNotFindByEmailAndPasswordIfUserDoesNotExist() {
        User user = userDao.save(getUser("test1@gmail.com"));

        Optional<User> actualResult = userDao.findByEmailAndPassword("dummy", "123");

        assertThat(actualResult).isEmpty();
    }

    @Test
    void deleteExistingEntity() {
        User user = userDao.save(getUser("test1@gmail.com"));

        boolean actualResult = userDao.delete(user.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingEntity() {
        userDao.save(getUser("test1@gmail.com"));

        boolean actualResult = userDao.delete(100500);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        User user = getUser("test1@gmail.com");
        userDao.save(user);
        user.setName("Ivan-updated");
        user.setPassword("new_password");

        userDao.update(user);

//        Проверяем изменение пользователя при помощи findById()
        User updatedUser = userDao.findById(user.getId()).get();
        assertThat(updatedUser).isEqualTo(user);
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