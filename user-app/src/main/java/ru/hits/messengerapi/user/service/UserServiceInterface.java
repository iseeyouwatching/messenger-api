package ru.hits.messengerapi.user.service;

import ru.hits.messengerapi.user.dto.UpdateUserInfoDto;
import ru.hits.messengerapi.user.dto.UserDto;
import ru.hits.messengerapi.user.dto.UserProfileDto;
import ru.hits.messengerapi.user.dto.UserSignUpDto;

public interface UserServiceInterface {

    UserDto userSignUp(UserSignUpDto userSignUpDto);

    UserProfileDto getUserInfo(String login);

//    UserDto updateUserInfo(String login, UpdateUserInfoDto updateUserInfoDto);

}
