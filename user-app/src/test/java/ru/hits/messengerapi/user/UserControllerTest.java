package ru.hits.messengerapi.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.user.controller.UserController;
import ru.hits.messengerapi.user.dto.*;
import ru.hits.messengerapi.user.service.UserService;

import java.net.UnknownHostException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController controller;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new UserController(userService);
    }

    @Test
    public void testUserSignUp() {
        UserSignUpDto signUpDto = new UserSignUpDto();
        signUpDto.setEmail("test@test.com");
        signUpDto.setLogin("test");
        signUpDto.setPassword("password");
        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setToken("token");
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto());

        when(userService.userSignUp(any(UserSignUpDto.class))).thenReturn(userProfileAndTokenDto);

        ResponseEntity<UserProfileDto> responseEntity = controller.userSignUp(signUpDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).contains("Bearer token"));
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    public void testUserSignIn() throws UnknownHostException {
        UserSignInDto signInDto = new UserSignInDto();
        signInDto.setLogin("test");
        signInDto.setPassword("password");
        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setToken("token");
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto());

        when(userService.userSignIn(any(UserSignInDto.class))).thenReturn(userProfileAndTokenDto);

        ResponseEntity<UserProfileDto> responseEntity = controller.userSignIn(signInDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).contains("Bearer token"));
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    public void testGetUsers() {
        PaginationDto paginationDto = new PaginationDto();
        UsersPageListDto usersPageListDto = new UsersPageListDto();

        when(userService.getUserList(any(PaginationDto.class))).thenReturn(usersPageListDto);

        ResponseEntity<UsersPageListDto> responseEntity = controller.getUsers(paginationDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    public void testGetUserInfo() {
        String login = "test";
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userService.getUserInfo(login)).thenReturn(userProfileDto);

        ResponseEntity<UserProfileDto> responseEntity = controller.getUserInfo(login);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    public void testViewYourProfile() {
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userService.viewYourProfile()).thenReturn(userProfileDto);

        ResponseEntity<UserProfileDto> response = controller.viewYourProfile();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userProfileDto, response.getBody());
        verify(userService, times(1)).viewYourProfile();
    }

    @Test
    public void testUpdateUserInfo() {
        UpdateUserInfoDto updateUserInfoDto = new UpdateUserInfoDto();
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userService.updateUserInfo(updateUserInfoDto)).thenReturn(userProfileDto);

        ResponseEntity<UserProfileDto> responseEntity = controller.updateUserInfo(updateUserInfoDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userProfileDto, responseEntity.getBody());
    }

}