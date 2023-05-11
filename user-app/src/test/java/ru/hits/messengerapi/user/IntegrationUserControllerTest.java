package ru.hits.messengerapi.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.common.dto.UserIdAndFullNameDto;
import ru.hits.messengerapi.user.controller.IntegrationUserController;
import ru.hits.messengerapi.user.service.IntegrationUserService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class IntegrationUserControllerTest {

    @Mock
    private IntegrationUserService integrationUserService;

    private IntegrationUserController integrationUserController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        integrationUserController = new IntegrationUserController(integrationUserService);
    }

    @Test
    void checkUserByIdAndFullName_shouldReturnExist() {
        UUID userId = UUID.randomUUID();
        String fullName = "John Doe";
        when(integrationUserService.checkUserByIdAndFullName(userId, fullName)).thenReturn(true);

        UserIdAndFullNameDto userIdAndFullNameDto = new UserIdAndFullNameDto(userId, fullName);
        ResponseEntity<Boolean> response = integrationUserController.checkUserByIdAndFullName(userIdAndFullNameDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
    }

    @Test
    void checkUserByIdAndFullName_shouldReturnDontExist() {
        UUID userId = UUID.randomUUID();
        String fullName = "John Doe";
        when(integrationUserService.checkUserByIdAndFullName(userId, fullName)).thenReturn(false);

        UserIdAndFullNameDto userIdAndFullNameDto = new UserIdAndFullNameDto(userId, fullName);
        ResponseEntity<Boolean> response = integrationUserController.checkUserByIdAndFullName(userIdAndFullNameDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
    }

    @Test
    void checkUserById_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        when(integrationUserService.checkUserById(userId)).thenReturn(true);

        ResponseEntity<Boolean> response = integrationUserController.checkUserById(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
    }

    @Test
    void checkUserById_shouldReturnFalse() {
        UUID userId = UUID.randomUUID();
        when(integrationUserService.checkUserById(userId)).thenReturn(false);

        ResponseEntity<Boolean> response = integrationUserController.checkUserById(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
    }

    @Test
    void getFullName_shouldReturnFullName() {
        UUID userId = UUID.randomUUID();
        String fullName = "John Doe";
        when(integrationUserService.getFullName(userId)).thenReturn(fullName);

        ResponseEntity<String> response = integrationUserController.getFullName(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(fullName);
    }

    @Test
    void getFullNameAndAvatar_shouldReturnFullNameAndAvatar() {
        UUID userId = UUID.randomUUID();
        List<String> fullNameAndAvatar = Arrays.asList("John Doe", UUID.randomUUID().toString());
        when(integrationUserService.getFullNameAndAvatar(userId)).thenReturn(fullNameAndAvatar);

        ResponseEntity<List<String>> response = integrationUserController.getFullNameAndAvatar(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(fullNameAndAvatar);
    }

}

