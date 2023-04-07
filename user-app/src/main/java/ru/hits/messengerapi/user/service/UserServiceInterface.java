package ru.hits.messengerapi.user.service;

import ru.hits.messengerapi.user.dto.*;


public interface UserServiceInterface {

    UserProfileAndTokenDto userSignUp(UserSignUpDto userSignUpDto);

    UserProfileAndTokenDto userSignIn(UserSignInDto userSignInDto);

    UserProfileDto getUserInfo(String login);

    UserProfileDto viewYourProfile();

//    UserDto updateUserInfo(String login, UpdateUserInfoDto updateUserInfoDto);

}
