package ru.hits.messengerapi.user.service;

import ru.hits.messengerapi.user.dto.UpdateUserInfoDto;
import ru.hits.messengerapi.user.dto.UserDto;
import ru.hits.messengerapi.user.dto.UserSignUpDto;

public interface UserServiceInterface {

    UserDto userSignUp(UserSignUpDto userSignUpDto);

    UserDto getUserInfo(String login);

    UserDto updateUserInfo(String login, UpdateUserInfoDto updateUserInfoDto);

}
