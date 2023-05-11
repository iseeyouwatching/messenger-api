package ru.hits.messengerapi.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JWTUtil;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.user.dto.UserProfileAndTokenDto;
import ru.hits.messengerapi.user.dto.UserSignUpDto;
import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.service.IntegrationRequestsService;
import ru.hits.messengerapi.user.service.UserService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTUtil jwtUtil;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * Вспомогательный сервис для проверки данных для пагинации.
     */
    private final CheckPaginationInfoService checkPaginationInfoService = new CheckPaginationInfoService();

    private final IntegrationRequestsService integrationRequestsService = new IntegrationRequestsService(new SecurityProps());
    private StreamBridge streamBridge;

    @BeforeEach
    void setUp() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, bCryptPasswordEncoder, jwtUtil,
                modelMapper, checkPaginationInfoService, integrationRequestsService, streamBridge);
    }

    @Test
    void testUserSignUp() {
        // Arrange
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setLogin("john");
        userSignUpDto.setEmail("john@example.com");
        userSignUpDto.setFullName("John Smith");
        userSignUpDto.setPassword("secret");
        userSignUpDto.setBirthDate(LocalDate.now().minusYears(18));

        UserEntity userEntity = new UserEntity();
        UUID uuid = UUID.randomUUID();
        userEntity.setId(uuid);
        userEntity.setLogin("john");
        userEntity.setEmail("john@example.com");
        userEntity.setFullName("John Smith");
        userEntity.setPassword(bCryptPasswordEncoder.encode("secret"));

        when(userRepository.findByLogin("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(jwtUtil.generateToken(uuid, "john", "John Smith")).thenReturn("token");

        UserProfileAndTokenDto result = userService.userSignUp(userSignUpDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUserProfileDto());
        assertEquals(uuid, result.getUserProfileDto().getId());
        assertEquals("john", result.getUserProfileDto().getLogin());
        assertEquals("John Smith", result.getUserProfileDto().getFullName());
        assertEquals("token", result.getToken());

        verify(userRepository).findByLogin("john");
        verify(userRepository).findByEmail("john@example.com");
        verify(userRepository).save(any(UserEntity.class));
        verify(jwtUtil).generateToken(uuid, "john", "John Smith");
    }

}
