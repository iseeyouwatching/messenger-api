package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.BadRequestException;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.exception.UnauthorizedException;
import ru.hits.messengerapi.common.JWTUtil;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.user.dto.*;

import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.service.UserServiceInterface;
import ru.hits.messengerapi.user.service.helpingservices.implementation.CheckPaginationInfoService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *  Сервис, предназначенный для бизнес-логики эндпоинтов пользователя.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final CheckPaginationInfoService checkPaginationInfoService;

    /**
     * Метод для регистрации пользователя.
     *
     * @param userSignUpDto DTO с данными нового пользователя.
     * @return объект класса {@link UserProfileAndTokenDto} с данными профиля пользователя и
     * сгенерированным JWT-токеном.
     * @throws ConflictException в случае, если пользователь с заданным логином/почтой уже существует.
     * @throws BadRequestException в случае, если дата рождения в DTO задана позже текущей даты.
     */
    @Override
    public UserProfileAndTokenDto userSignUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByLogin(userSignUpDto.getLogin()).isPresent()) {
            throw new ConflictException("Пользователь с логином " + userSignUpDto.getLogin() + " уже существует.");
        }

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new ConflictException("Пользователь с почтой " + userSignUpDto.getEmail() + " уже существует.");
        }

        if (userSignUpDto.getBirthDate() != null && userSignUpDto.getBirthDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Дата рождения не может быть позже текущей.");
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
    @Override
    public UserProfileAndTokenDto userSignIn(UserSignInDto userSignInDto) {
        Optional<UserEntity> user = userRepository.findByLogin(userSignInDto.getLogin());

        if (user.isEmpty() ||
                !bCryptPasswordEncoder.matches(userSignInDto.getPassword(), user.get().getPassword())) {
            throw new UnauthorizedException("Некорректные данные.");
        }

        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto(user.get()));
        userProfileAndTokenDto.setToken(jwtUtil.generateToken(
                user.get().getId(),
                user.get().getLogin(),
                user.get().getFullName()))
        ;

        return userProfileAndTokenDto;
    }

    /**
     * Метод для получения списка пользователей с учетом фильтрации, сортировки и постраничной навигации.
     *
     * @param paginationDto DTO объект, содержащий информацию о постраничной навигации, фильтрации и сортировке.
     * @return объект класса {@link UsersPageListDto}, содержащий список пользователей, соответствующий запросу и
     * информацию о постраничной навигации, фильтрации и сортировке.
     */
    @Override
    public UsersPageListDto getUserList(PaginationDto paginationDto) {
        int pageNumber = paginationDto.getPageInfo().getPageNumber();
        int pageSize = paginationDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);

        Pageable pageable;
        if (paginationDto.getSortings() != null) {
            List<SortingDto> sortings = paginationDto.getSortings();
            List<Order> orders = new ArrayList<>();
            for (SortingDto sorting : sortings) {
                orders.add(new Order(Sort.Direction.fromString(sorting.getDirection().toString()),
                        sorting.getField().toString()));
            }
            pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(orders));
        }
        else {
            pageable = PageRequest.of(pageNumber - 1, pageSize);
        }

        Page<UserEntity> users;
        if (paginationDto.getFilters() != null) {
            FiltersDto filtersDto = paginationDto.getFilters();

            Example<UserEntity> example = Example.of(UserEntity
                    .builder()
                    .login(filtersDto.getLogin())
                    .email(filtersDto.getEmail())
                    .fullName(filtersDto.getFullName())
                    .birthDate(filtersDto.getBirthDate())
                    .phoneNumber(filtersDto.getPhoneNumber())
                    .city(filtersDto.getCity())
                    .build());

            users = userRepository.findAll(example, pageable);
        }
        else {
            users = userRepository.findAll(pageable);
        }

        List<UserEntity> userEntities = users.getContent();
        List<UserProfileDto> userProfileDtos = new ArrayList<>();

        for (UserEntity userEntity: userEntities) {
            userProfileDtos.add(new UserProfileDto(userEntity));
        }

        UsersPageListDto usersPageListDto = new UsersPageListDto();

        usersPageListDto.setUsers(userProfileDtos);
        usersPageListDto.setPageInfo(paginationDto.getPageInfo());
        usersPageListDto.setFilters(paginationDto.getFilters());
        usersPageListDto.setSortings(paginationDto.getSortings());

        return usersPageListDto;
    }

    /**
     * Метод для получения информации о пользователе по его логину.
     *
     * @param login логин пользователя.
     * @return объект класса {@link UserProfileDto}, содержащий информацию о пользователе с указанным логином.
     * @throws NotFoundException если пользователь с указанным логином не найден.
     */
    @Override
    public UserProfileDto getUserInfo(String login) {
        Optional<UserEntity> user = userRepository.findByLogin(login);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с логином " + login + " не найден.");
        }

        return new UserProfileDto(user.get());
    }

    /**
     * Метод для получения информации о профиле текущего пользователя.
     *
     * @return объект класса {@link UserProfileDto} с информацией о профиле текущего пользователя.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     */
    @Override
    public UserProfileDto viewYourProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID id = userData.getId();
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
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
    @Override
    public UserProfileDto updateUserInfo(UpdateUserInfoDto updateUserInfoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID id = userData.getId();
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        if (updateUserInfoDto.getBirthDate() != null &&
                updateUserInfoDto.getBirthDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Дата рождения не может быть позже текущей.");
        }

        if (updateUserInfoDto.getFullName() != null) {
            user.get().setFullName(updateUserInfoDto.getFullName());
        }

        if (updateUserInfoDto.getBirthDate() != null) {
            user.get().setBirthDate(updateUserInfoDto.getBirthDate());
        }

        if (updateUserInfoDto.getPhoneNumber() != null) {
            user.get().setPhoneNumber(updateUserInfoDto.getPhoneNumber());
        }

        if (updateUserInfoDto.getCity() != null) {
            user.get().setCity(updateUserInfoDto.getCity());
        }

        if (updateUserInfoDto.getAvatar() != null) {
            user.get().setAvatar(updateUserInfoDto.getAvatar());
        }

        user = Optional.of(userRepository.save(user.get()));

        return new UserProfileDto(user.get());
    }

}
