package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.exception.UnauthorizedException;
import ru.hits.messengerapi.user.dto.*;
import ru.hits.messengerapi.user.entity.UserEntity;

import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.security.JWTUtil;
import ru.hits.messengerapi.user.service.UserServiceInterface;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;

    @Override
    public UserProfileAndTokenDto userSignUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByLogin(userSignUpDto.getLogin()).isPresent()) {
            throw new ConflictException("Пользователь с логином " + userSignUpDto.getLogin() + " уже существует.");
        }

        UserEntity user = modelMapper.map(userSignUpDto, UserEntity.class);
        user.setPassword(passwordEncoder.encode(userSignUpDto.getPassword()));

        user = userRepository.save(user);

        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto(user));
        userProfileAndTokenDto.setToken(jwtUtil.generateToken(user.getId()));

        return userProfileAndTokenDto;
    }

    @Override
    public UserProfileAndTokenDto userSignIn(UserSignInDto userSignInDto) {
        Optional<UserEntity> user = userRepository.findByLogin(userSignInDto.getLogin());

        if (user.isEmpty() ||
                !passwordEncoder.matches(userSignInDto.getPassword(), user.get().getPassword())) {
            throw new UnauthorizedException("Некорректные данные.");
        }

        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto(user.get()));
        userProfileAndTokenDto.setToken(jwtUtil.generateToken(user.get().getId()));

        return userProfileAndTokenDto;
    }

    @Override
    public UserProfileDto getUserInfo(String login) {
        Optional<UserEntity> user = userRepository.findByLogin(login);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с логином " + login + " не найден.");
        }

        return new UserProfileDto(user.get());
    }

    @Override
    public UserProfileDto viewYourProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID id = UUID.fromString(authentication.getName());
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
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
