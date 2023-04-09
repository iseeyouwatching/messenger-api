package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.exception.UnauthorizedException;
import ru.hits.messengerapi.user.dto.*;
import ru.hits.messengerapi.user.entity.UserEntity;

import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.security.JWTUtil;
import ru.hits.messengerapi.user.service.UserServiceInterface;
import ru.hits.messengerapi.user.service.helpingservices.implementation.CheckPaginationInfoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final CheckPaginationInfoService checkPaginationInfoService;

    @Override
    public UserProfileAndTokenDto userSignUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByLogin(userSignUpDto.getLogin()).isPresent()) {
            throw new ConflictException("Пользователь с логином " + userSignUpDto.getLogin() + " уже существует.");
        }

        UserEntity user = modelMapper.map(userSignUpDto, UserEntity.class);
        user.setPassword(passwordEncoder.encode(userSignUpDto.getPassword()));

        user = userRepository.save(user);

        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto(user));
        userProfileAndTokenDto.setToken(jwtUtil.generateToken(user.getId()));

        return userProfileAndTokenDto;
    }

    @Override
    public UserProfileAndTokenDto userSignIn(UserSignInDto userSignInDto) {
        Optional<UserEntity> user = userRepository.findByLogin(userSignInDto.getLogin());

        if (user.isEmpty() ||
                !passwordEncoder.matches(userSignInDto.getPassword(), user.get().getPassword())) {
            throw new UnauthorizedException("Некорректные данные.");
        }

        UserProfileAndTokenDto userProfileAndTokenDto = new UserProfileAndTokenDto();
        userProfileAndTokenDto.setUserProfileDto(new UserProfileDto(user.get()));
        userProfileAndTokenDto.setToken(jwtUtil.generateToken(user.get().getId()));

        return userProfileAndTokenDto;
    }

    @Override
    public UsersPageListDto getUserList(PaginationDto paginationDto) {
        int pageNumber = paginationDto.getPageInfo().getPageNumber();
        int pageSize = paginationDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);

        Pageable pageable;
        if (paginationDto.getSorting() != null) {
            List<SortingDto> sortings = paginationDto.getSorting();
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
        usersPageListDto.setPagination(paginationDto.getPageInfo());
        usersPageListDto.setFilters(paginationDto.getFilters());
        usersPageListDto.setSorting(paginationDto.getSorting());

        return usersPageListDto;
    }

    @Override
    public UserProfileDto getUserInfo(String login) {
        Optional<UserEntity> user = userRepository.findByLogin(login);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с логином " + login + " не найден.");
        }

        return new UserProfileDto(user.get());
    }

    @Override
    public UserProfileDto viewYourProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID id = UUID.fromString(authentication.getName());
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        return new UserProfileDto(user.get());
    }

    @Override
    public UserProfileDto updateUserInfo(UpdateUserInfoDto updateUserInfoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID id = UUID.fromString(authentication.getName());
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
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
