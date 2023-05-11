package ru.hits.messengerapi.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.service.IntegrationUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class IntegrationUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IntegrationUserService integrationUserService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckUserByIdAndFullName_userExists() {
        UUID id = UUID.randomUUID();
        String fullName = "John Doe";

        when(userRepository.findByIdAndFullName(id, fullName)).thenReturn(Optional.of(new UserEntity()));

        boolean result = integrationUserService.checkUserByIdAndFullName(id, fullName);

        Assertions.assertTrue(result);
    }

    @Test
    public void testCheckUserByIdAndFullName_userDoesNotExist() {
        UUID id = UUID.randomUUID();
        String fullName = "John Doe";

        when(userRepository.findByIdAndFullName(id, fullName)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            integrationUserService.checkUserByIdAndFullName(id, fullName);
        });
    }

    @Test
    public void testCheckUserById_userExists() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.of(new UserEntity()));

        boolean result = integrationUserService.checkUserById(id);

        Assertions.assertTrue(result);
    }

    @Test
    public void testCheckUserById_userDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            integrationUserService.checkUserById(id);
        });
    }

    @Test
    public void testGetFullName_userExists() {
        UUID id = UUID.randomUUID();
        String fullName = "John Doe";

        UserEntity user = UserEntity.builder().fullName(fullName).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        String result = integrationUserService.getFullName(id);

        assertEquals(fullName, result);
    }

    @Test
    public void testGetFullName_userDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            integrationUserService.getFullName(id);
        });
    }

    @Test
    public void testGetFullNameAndAvatar_userExistsWithoutAvatar() {
        UUID id = UUID.randomUUID();
        String fullName = "John Doe";

        UserEntity user = UserEntity.builder().fullName(fullName).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        List<String> result = integrationUserService.getFullNameAndAvatar(id);

        List<String> expected = new ArrayList<>();
        expected.add(fullName);
        expected.add(null);

        assertEquals(expected, result);
    }

    @Test
    public void testGetFullNameAndAvatar_userExistsWithAvatar() {
        UUID id = UUID.randomUUID();
        String fullName = "John Doe";
        UUID avatar = UUID.randomUUID();

        UserEntity user = UserEntity.builder().fullName(fullName).avatar(avatar).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        List<String> result = integrationUserService.getFullNameAndAvatar(id);

        List<String> expected = new ArrayList<>();
        expected.add(fullName);
        expected.add(String.valueOf(avatar));

        assertEquals(expected, result);
    }

    @Test
    public void testGetFullNameAndAvatar_userDoesNotExist() {
        UUID id = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> integrationUserService.getFullNameAndAvatar(id));
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
    }

    /**
     * Test for the getFullNameAndAvatar method when the user has no avatar.
     */
    @Test
    public void testGetFullNameAndAvatar_userHasNoAvatar() {
        UUID id = UUID.randomUUID();
        String fullName = "John Smith";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setFullName(fullName);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        List<String> result = integrationUserService.getFullNameAndAvatar(id);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(fullName, result.get(0));
        assertNull(result.get(1));

        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
    }

    /**
     * Test for the getFullNameAndAvatar method when the user has an avatar.
     */
    @Test
    public void testGetFullNameAndAvatar_userHasAvatar() {
        UUID id = UUID.randomUUID();
        String fullName = "John Smith";
        UUID avatar = UUID.randomUUID();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setFullName(fullName);
        userEntity.setAvatar(avatar);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        List<String> result = integrationUserService.getFullNameAndAvatar(id);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(fullName, result.get(0));
        assertEquals(String.valueOf(avatar), result.get(1));

        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
    }
}
