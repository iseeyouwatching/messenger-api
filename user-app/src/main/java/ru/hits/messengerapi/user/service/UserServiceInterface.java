package ru.hits.messengerapi.user.service;

import ru.hits.messengerapi.user.dto.*;

public interface UserServiceInterface {

    UserDto userSignUp(UserSignUpDto userSignUpDto);

    UserDto userSignIn(UserSignInDto userSignInDto);

    UserProfileDto getUserInfo(String login);

//    UserDto updateUserInfo(String login, UpdateUserInfoDto updateUserInfoDto);

}
