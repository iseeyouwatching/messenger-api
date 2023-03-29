package ru.hits.messengerapi.service;

import ru.hits.messengerapi.dto.UpdateUserInfoDto;
import ru.hits.messengerapi.dto.UserDto;
import ru.hits.messengerapi.dto.UserSignUpDto;

public interface UserServiceInterface {

    UserDto userSignUp(UserSignUpDto userSignUpDto);

    UserDto getUserInfo(String login);

    UserDto updateUserInfo(String login, UpdateUserInfoDto updateUserInfoDto);

}
