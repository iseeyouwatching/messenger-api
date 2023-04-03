package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.BadRequestException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.user.dto.UpdateUserInfoDto;
import ru.hits.messengerapi.user.dto.UserDto;
import ru.hits.messengerapi.user.dto.UserSignUpDto;
import ru.hits.messengerapi.user.entity.UserEntity;

import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.service.UserServiceInterface;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;

    @Override
    public UserDto userSignUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByLogin(userSignUpDto.getLogin()) != null) {
            throw new BadRequestException("Пользователь с логином " + userSignUpDto.getLogin() + " уже существует.");
        }

        UserEntity user = new UserEntity();

        user.setName(userSignUpDto.getName());
        user.setSurname(userSignUpDto.getSurname());
        user.setPatronymic(userSignUpDto.getPatronymic());
        user.setEmail(userSignUpDto.getEmail());
        user.setLogin(userSignUpDto.getLogin());
        user.setPassword(userSignUpDto.getPassword());
        user.setSex(userSignUpDto.getSex());
        user.setBirthdate(userSignUpDto.getBirthdate());
        user.setRegistrationDate(LocalDate.now());

        userRepository.save(user);

        return new UserDto(user);
    }

    @Override
    public UserDto getUserInfo(String login) {
        UserEntity user = userRepository.findByLogin(login);

        if (user == null) {
            throw new NotFoundException("Пользователь с логином " + login + " не найден.");
        }

        return new UserDto(user);
    }

    @Override
    public UserDto updateUserInfo(String login, UpdateUserInfoDto updateUserInfoDto) {
        UserEntity user = userRepository.findByLogin(login);

        if (user == null) {
            throw new NotFoundException("Пользователь с логином " + login + " не найден.");
        }

        if (updateUserInfoDto.getName() != null) {
            user.setName(updateUserInfoDto.getName());
        }

        if (updateUserInfoDto.getSurname() != null) {
            user.setSurname(updateUserInfoDto.getSurname());
        }

        if (updateUserInfoDto.getPatronymic() != null) {
            user.setPatronymic(updateUserInfoDto.getPatronymic());
        }

        if (updateUserInfoDto.getEmail() != null) {
            user.setEmail(updateUserInfoDto.getEmail());
        }

        if (updateUserInfoDto.getPassword() != null) {
            user.setPassword(updateUserInfoDto.getPassword());
        }

        if (updateUserInfoDto.getSex() != null) {
            user.setSex(updateUserInfoDto.getSex());
        }

        if (updateUserInfoDto.getBirthdate() != null) {
            user.setBirthdate(updateUserInfoDto.getBirthdate());
        }

        userRepository.save(user);

        return new UserDto(user);
    }
}
