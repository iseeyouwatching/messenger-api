package ru.hits.messengerapi.user.service;

import ru.hits.messengerapi.user.dto.*;

import java.util.Map;

public interface UserServiceInterface {

    Map<String, String> userSignUp(UserSignUpDto userSignUpDto);

    Map<String, String> userSignIn(UserSignInDto userSignInDto);

    UserProfileDto getUserInfo(String login);

//    UserDto updateUserInfo(String login, UpdateUserInfoDto updateUserInfoDto);

}
