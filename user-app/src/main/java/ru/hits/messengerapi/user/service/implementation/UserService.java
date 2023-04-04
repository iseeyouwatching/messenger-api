package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.exception.UnauthorizedException;
import ru.hits.messengerapi.user.dto.UserDto;
import ru.hits.messengerapi.user.dto.UserProfileDto;
import ru.hits.messengerapi.user.dto.UserSignInDto;
import ru.hits.messengerapi.user.dto.UserSignUpDto;
import ru.hits.messengerapi.user.entity.UserEntity;

import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.service.UserServiceInterface;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto userSignUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByLogin(userSignUpDto.getLogin()).isPresent()) {
            throw new ConflictException("Пользователь с логином " + userSignUpDto.getLogin() + " уже существует.");
        }

        UserEntity user = new UserEntity();

        user.setLogin(userSignUpDto.getLogin());
        user.setEmail(userSignUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(userSignUpDto.getPassword()));
        user.setFullName(userSignUpDto.getFullName());
        user.setBirthDate(userSignUpDto.getBirthDate());
        user.setPhoneNumber(userSignUpDto.getPhoneNumber());
        user.setCity(userSignUpDto.getCity());
        user.setAvatar(userSignUpDto.getAvatar());

        userRepository.save(user);

        return new UserDto(user);
    }

    @Override
    public UserDto userSignIn(UserSignInDto userSignInDto) {
        Optional<UserEntity> user = userRepository.findByLogin(userSignInDto.getLogin());

        if (user.isEmpty() ||
                !passwordEncoder.matches(userSignInDto.getPassword(), user.get().getPassword())) {
            throw new UnauthorizedException("Некорректные данные.");
        }

        return new UserDto(user.get());
    }

    @Override
    public UserProfileDto getUserInfo(String login) {
        Optional<UserEntity> user = userRepository.findByLogin(login);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с логином " + login + " не найден.");
        }

        return new UserProfileDto(user.get());
    }

//    @Override
//    public UserDto updateUserInfo(String login, UpdateUserInfoDto updateUserInfoDto) {
//        UserEntity user = userRepository.findByLogin(login);
//
//        if (user == null) {
//            throw new NotFoundException("Пользователь с логином " + login + " не найден.");
//        }
//
//        if (updateUserInfoDto.getName() != null) {
//            user.setName(updateUserInfoDto.getName());
//        }
//
//        if (updateUserInfoDto.getSurname() != null) {
//            user.setSurname(updateUserInfoDto.getSurname());
//        }
//
//        if (updateUserInfoDto.getPatronymic() != null) {
//            user.setPatronymic(updateUserInfoDto.getPatronymic());
//        }
//
//        if (updateUserInfoDto.getEmail() != null) {
//            user.setEmail(updateUserInfoDto.getEmail());
//        }
//
//        if (updateUserInfoDto.getPassword() != null) {
//            user.setPassword(updateUserInfoDto.getPassword());
//        }
//
//        if (updateUserInfoDto.getSex() != null) {
//            user.setSex(updateUserInfoDto.getSex());
//        }
//
//        if (updateUserInfoDto.getBirthdate() != null) {
//            user.setBirthdate(updateUserInfoDto.getBirthdate());
//        }
//
//        userRepository.save(user);
//
//        return new UserDto(user);
//    }

}
