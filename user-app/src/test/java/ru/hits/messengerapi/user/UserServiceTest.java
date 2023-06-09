package ru.hits.messengerapi.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JWTUtil;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.common.security.props.SecurityIntegrationsProps;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.user.dto.*;
import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.service.IntegrationRequestsService;
import ru.hits.messengerapi.user.service.UserService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
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

    private IntegrationRequestsService integrationRequestsService;

    private StreamBridge streamBridge;
    private SecurityProps securityProps = new SecurityProps(null, new SecurityIntegrationsProps("ac816388c1c44ac2b2ae431f89c82e7e345d25a0e6474e75a78f9a5ce496060c", null));

    @BeforeEach
    void setUp() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        integrationRequestsService = new IntegrationRequestsService(securityProps);
        userService = new UserService(userRepository, bCryptPasswordEncoder, jwtUtil,
                modelMapper, checkPaginationInfoService, integrationRequestsService, streamBridge);
    }

    @Test
    void testUserSignUp() {
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

    @Test
    void testGetUserList_WithFiltersAndSortings() {
        int pageNumber = 1;
        int pageSize = 10;
        PageInfoDto pageInfoDto = new PageInfoDto(pageNumber, pageSize);
        FiltersDto filtersDto = new FiltersDto();
        filtersDto.setLogin("john");
        filtersDto.setEmail("john@example.com");
        PaginationDto paginationDto =
                new PaginationDto(
                        pageInfoDto,
                        filtersDto,
                        new ArrayList<>());

        List<UserEntity> userEntities = Collections.singletonList(new UserEntity());
        Page<UserEntity> userEntityPage = new PageImpl<>(userEntities);
        when(userRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(userEntityPage);

        UsersPageListDto result = userService.getUserList(paginationDto);

        assertEquals(userEntities.size(), result.getUsers().size());
        assertEquals(pageInfoDto, result.getPageInfo());
        assertEquals(filtersDto, result.getFilters());
        assertEquals(new ArrayList<>(), result.getSortings());
    }

    @Test
    void getUserInfo_nonExistingUser_shouldThrowNotFoundException() {
        String login = "john.doe";
        Optional<UserEntity> userOptional = Optional.empty();

        when(userRepository.findByLogin(login)).thenReturn(userOptional);

        assertThrows(NotFoundException.class, () -> userService.getUserInfo(login));
    }

    @Test
    void viewYourProfile_existingUser_shouldReturnUserProfileDto() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        Optional<UserEntity> userOptional = Optional.of(userEntity);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", userId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        when(userRepository.findById(userId)).thenReturn(userOptional);

        UserProfileDto userProfileDto = userService.viewYourProfile();

        assertNotNull(userProfileDto);
        assertEquals(userEntity.getId(), userProfileDto.getId());
    }

    @Test
    void viewYourProfile_nonExistingUser_shouldThrowNotFoundException() {
        UUID userId = UUID.randomUUID();
        Optional<UserEntity> userOptional = Optional.empty();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", userId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        when(userRepository.findById(userId)).thenReturn(userOptional);

        assertThrows(NotFoundException.class, () -> userService.viewYourProfile());
    }

    @Test
    void getUserById_existingUser_shouldReturnUserEntity() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        Optional<UserEntity> userOptional = Optional.of(userEntity);

        when(userRepository.findById(userId)).thenReturn(userOptional);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", userId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        Method getUserByIdMethod = UserService.class.getDeclaredMethod("getUserById");
        getUserByIdMethod.setAccessible(true);

        UserEntity result = (UserEntity) getUserByIdMethod.invoke(userService);

        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
    }

    @Test
    void getUserById_nonExistingUser_shouldThrowNotFoundException() throws NoSuchMethodException {
        UUID userId = UUID.randomUUID();
        Optional<UserEntity> userOptional = Optional.empty();

        when(userRepository.findById(userId)).thenReturn(userOptional);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", userId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        Method getUserByIdMethod = UserService.class.getDeclaredMethod("getUserById");
        getUserByIdMethod.setAccessible(true);

        try {
            getUserByIdMethod.invoke(userService);
            fail("Должно быть выброшено исключение NotFoundException.");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertEquals(NotFoundException.class, cause.getClass());
            assertEquals("Пользователь с ID " + userId + " не найден.", cause.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
