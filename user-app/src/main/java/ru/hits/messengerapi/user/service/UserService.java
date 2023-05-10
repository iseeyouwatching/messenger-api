package ru.hits.messengerapi.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.messengerapi.common.dto.NewNotificationDto;
import ru.hits.messengerapi.common.enumeration.NotificationType;
import ru.hits.messengerapi.common.exception.BadRequestException;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.exception.UnauthorizedException;
import ru.hits.messengerapi.common.security.JWTUtil;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.user.dto.*;

import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *  Сервис пользователя.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;

    /**
     * Шифровальщик паролей.
     */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Вспомогательный сервис для работы с JWT-токенами.
     */
    private final JWTUtil jwtUtil;

    /**
     * Маппер.
     */
    private final ModelMapper modelMapper;

    /**
     * Вспомогательный сервис для проверки данных для пагинации.
     */
    private final CheckPaginationInfoService checkPaginationInfoService;

    private final IntegrationRequestsService integrationRequestsService;
    private final StreamBridge streamBridge;

    /**
     * Метод для регистрации пользователя.
     *
     * @param userSignUpDto DTO с данными нового пользователя.
     * @return объект класса {@link UserProfileAndTokenDto} с данными профиля пользователя и
     * сгенерированным JWT-токеном.
     * @throws ConflictException в случае, если пользователь с заданным логином/почтой уже существует.
     * @throws BadRequestException в случае, если дата рождения в DTO задана позже текущей даты.
     */
    @Transactional
    public UserProfileAndTokenDto userSignUp(UserSignUpDto userSignUpDto) {
        if (userRepository.findByLogin(userSignUpDto.getLogin()).isPresent()) {
            log.warn("Пользователь с логином {} уже существует.", userSignUpDto.getLogin());
            throw new ConflictException("Пользователь с логином " + userSignUpDto.getLogin() + " уже существует.");
        }

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            log.warn("Пользователь с почтой {} уже существует.", userSignUpDto.getEmail());
            throw new ConflictException("Пользователь с почтой " + userSignUpDto.getEmail() + " уже существует.");
        }

        LocalDate birthDate = userSignUpDto.getBirthDate();
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть позже текущей.";
            log.warn(message);
            throw new BadRequestException(message);
        }

        UserEntity user = modelMapper.map(userSignUpDto, UserEntity.class);
        user.setPassword(bCryptPasswordEncoder.encode(userSignUpDto.getPassword()));
        user = userRepository.save(user);

        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto(user));
        userProfileAndTokenDto.setToken(jwtUtil.generateToken(user.getId(), user.getLogin(), user.getFullName()));

        return userProfileAndTokenDto;
    }

    /**
     * Метод для аутентификации пользователя.
     *
     * @param userSignInDto DTO с данными для входа пользователя.
     * @return объект класса {@link UserProfileAndTokenDto} с данными профиля пользователя и
     * сгенерированным JWT-токеном.
     * @throws UnauthorizedException в случае, если заданы неверные данные для входа.
     */
    public UserProfileAndTokenDto userSignIn(UserSignInDto userSignInDto) throws UnknownHostException {
        Optional<UserEntity> user = userRepository.findByLogin(userSignInDto.getLogin());

        if (user.isEmpty() ||
                !bCryptPasswordEncoder.matches(userSignInDto.getPassword(), user.get().getPassword())) {
            String message = "Некорректные данные.";
            log.error(message);
            throw new UnauthorizedException(message);
        }

        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto(user.get()));
        userProfileAndTokenDto.setToken(jwtUtil.generateToken(
                user.get().getId(),
                user.get().getLogin(),
                user.get().getFullName())
        );

        log.info("Пользователь с логином {} авторизовался в системе.", user.get().getLogin());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        NewNotificationDto newNotificationDto = NewNotificationDto.builder()
                .userId(user.get().getId())
                .type(NotificationType.LOGIN)
                .text("Был выполнен вход в систему в "  + formattedDateTime +
                        " с использованием IP-адреса " + InetAddress.getLocalHost().getHostAddress() + ".")
                .build();
        sendByStreamBridge(newNotificationDto);

        return userProfileAndTokenDto;
    }

    /**
     * Метод для получения списка пользователей с учетом фильтрации, сортировки и постраничной навигации.
     *
     * @param paginationDto DTO объект, содержащий информацию о постраничной навигации, фильтрации и сортировке.
     * @return объект класса {@link UsersPageListDto}, содержащий список пользователей, соответствующий запросу и
     * информацию о постраничной навигации, фильтрации и сортировке.
     */
    public UsersPageListDto getUserList(PaginationDto paginationDto) {
        int pageNumber = paginationDto.getPageInfo().getPageNumber();
        int pageSize = paginationDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        log.info("Запрос списка пользователей. Страница: {}, размер страницы: {}",
                paginationDto.getPageInfo().getPageNumber(), paginationDto.getPageInfo().getPageSize());

        Pageable pageable;
        if (paginationDto.getSortings() != null) {
            List<SortingDto> sortings = paginationDto.getSortings();
            List<Order> orders = sortings.stream()
                    .map(s -> new Order(Sort.Direction.fromString(s.getDirection()), s.getField()))
                    .collect(Collectors.toList());
            pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(orders));
        }
        else {
            pageable = PageRequest.of(pageNumber - 1, pageSize);
        }

        Example<UserEntity> example = null;
        if (paginationDto.getFilters() != null) {
            FiltersDto filtersDto = paginationDto.getFilters();
            UserEntity userEntity = UserEntity.builder()
                    .login(filtersDto.getLogin())
                    .email(filtersDto.getEmail())
                    .fullName(filtersDto.getFullName())
                    .birthDate(filtersDto.getBirthDate())
                    .phoneNumber(filtersDto.getPhoneNumber())
                    .city(filtersDto.getCity())
                    .build();
            example = Example.of(userEntity);
        }

        Page<UserEntity> page = example == null ? userRepository.findAll(pageable) :
                userRepository.findAll(example, pageable);

        List<UserProfileDto> userProfileDtos = page.getContent().stream()
                .map(UserProfileDto::new)
                .collect(Collectors.toList());

        UsersPageListDto usersPageListDto = new UsersPageListDto();
        usersPageListDto.setUsers(userProfileDtos);
        usersPageListDto.setPageInfo(paginationDto.getPageInfo());
        usersPageListDto.setFilters(paginationDto.getFilters());
        usersPageListDto.setSortings(paginationDto.getSortings());

        log.info("Список пользователей успешно получен.");

        return usersPageListDto;
    }

    /**
     * Метод для получения информации о пользователе по его логину.
     *
     * @param login логин пользователя.
     * @return объект класса {@link UserProfileDto}, содержащий информацию о пользователе с указанным логином.
     * @throws NotFoundException если пользователь с указанным логином не найден.
     */
    public UserProfileDto getUserInfo(String login) {
        Optional<UserEntity> user = userRepository.findByLogin(login);
        if (user.isEmpty()) {
            log.warn("Пользователь с логином {} не найден.", login);
            throw new NotFoundException("Пользователь с логином " + login + " не найден.");
        }

        UUID id = getAuthenticatedUserId();
        if (integrationRequestsService.checkExistenceInBlacklist(user.get().getId(), id)) {
            log.warn("Пользователь с ID {} не может посмотреть профиль пользователя с ID {}, " +
                    "так как находится у него в черном списке.", id, user.get().getId());
            throw new ConflictException("Пользователь с ID " + id
                    + " не может посмотреть профиль пользователя с ID " + user.get().getId()
                    + ", так как находится у него в черном списке.");
        }

        log.info("Информация о пользователе получена.");

        return new UserProfileDto(user.get());
    }

    /**
     * Метод для получения информации о профиле текущего пользователя.
     *
     * @return объект класса {@link UserProfileDto} с информацией о профиле текущего пользователя.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     */
    public UserProfileDto viewYourProfile() {
        UUID id = getAuthenticatedUserId();
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            log.error("Пользователь с ID {} не найден.", id);
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        return new UserProfileDto(user.get());
    }

    /**
     * Метод для обновления информации о профиле текущего пользователя.
     *
     * @param updateUserInfoDto объект класса {@link UpdateUserInfoDto} с обновленными данными пользователя.
     * @return объект класса {@link UserProfileDto} с обновленной информацией о профиле текущего пользователя.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     * @throws BadRequestException если дата рождения позже текущей даты.
     */
    @Transactional
    public UserProfileDto updateUserInfo(UpdateUserInfoDto updateUserInfoDto) {
        UserEntity user = getUserById();
        validateFields(updateUserInfoDto);
        updateUserEntity(user, updateUserInfoDto);

        UserEntity savedUser = userRepository.save(user);

        log.info("Профиль пользователя с ID {} успешно обновлен.", user.getId());

        return new UserProfileDto(savedUser);
    }

    /**
     * Метод для получения сущности аутентифицированного пользователя по его ID.
     *
     * @return сущность аутентифицированного пользователя.
     * @throws NotFoundException если пользователь не найден.
     */
    private UserEntity getUserById() {
        UUID id = getAuthenticatedUserId();
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            log.error("Пользователь с ID {} не найден.", id);
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        return user.get();
    }

    /**
     * Метод для валидирования полей DTO на обновление данных пользователя.
     *
     * @param updateUserInfoDto DTO на обновление данных пользователя.
     * @throws BadRequestException если дата рождения позже текущей.
     */
    private void validateFields(UpdateUserInfoDto updateUserInfoDto) {
        if (updateUserInfoDto.getBirthDate() != null &&
                updateUserInfoDto.getBirthDate().isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть позже текущей.";
            log.error(message);
            throw new BadRequestException(message);
        }
    }

    /**
     * Метод необходимый для обновления данных в сущности пользователя.
     *
     * @param user сущность пользователя.
     * @param updateUserInfoDto DTO, содержащая данные для обновления данных пользователя.
     */
    private void updateUserEntity(UserEntity user, UpdateUserInfoDto updateUserInfoDto) {
        if (updateUserInfoDto.getFullName() != null) {
            user.setFullName(updateUserInfoDto.getFullName());
        }

        if (updateUserInfoDto.getBirthDate() != null) {
            user.setBirthDate(updateUserInfoDto.getBirthDate());
        }

        if (updateUserInfoDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserInfoDto.getPhoneNumber());
        }

        if (updateUserInfoDto.getCity() != null) {
            user.setCity(updateUserInfoDto.getCity());
        }

        if (updateUserInfoDto.getAvatar() != null) {
            user.setAvatar(updateUserInfoDto.getAvatar());
        }
    }

    /**
     * Метод для получения ID аутентифицированного пользователя.
     *
     * @return ID аутентифицированного пользователя.
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

    /**
     * Отправляет объект типа {@link NewNotificationDto} посредством StreamBridge.
     *
     * @param newNotificationDto объект класса {@link NewNotificationDto},
     *                           содержащий информацию о новом уведомлении.
     */
    private void sendByStreamBridge(NewNotificationDto newNotificationDto) {
        streamBridge.send("newNotificationEvent-out-0", newNotificationDto);
    }

}
