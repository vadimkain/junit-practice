package com.kainv.mapper;

import com.kainv.dto.CreateUserDto;
import com.kainv.entity.Gender;
import com.kainv.entity.Role;
import com.kainv.entity.User;
import com.kainv.util.LocalDateFormatter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CreateUserMapper implements Mapper<CreateUserDto, User> {

    private static final CreateUserMapper INSTANCE = new CreateUserMapper();

    public static CreateUserMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public User map(CreateUserDto object) {
        return User.builder()
                .name(object.getName())
                .birthday(LocalDateFormatter.format(object.getBirthday()))
                .email(object.getEmail())
                .password(object.getPassword())
                .gender(Gender.find(object.getGender()).orElse(null))
                .role(Role.find(object.getRole()).orElse(null))
                .build();
    }
}
