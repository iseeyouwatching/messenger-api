package ru.hits.messengerapi.user.service;

import ru.hits.messengerapi.common.exception.BadRequestException;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.exception.UnauthorizedException;
import ru.hits.messengerapi.user.dto.*;

/**
 *  Интерфейс для сервиса пользователя.
 */
public interface UserServiceInterface {

    /**
     * Метод для регистрации пользователя.
     *
     * @param userSignUpDto DTO с данными нового пользователя.
     * @return объект класса {@link UserProfileAndTokenDto} с данными профиля пользователя и
     * сгенерированным JWT-токеном.
     * @throws ConflictException в случае, если пользователь с заданным логином/почтой уже существует.
     * @throws BadRequestException в случае, если дата рождения в DTO задана позже текущей даты.
     */
    UserProfileAndTokenDto userSignUp(UserSignUpDto userSignUpDto);

    /**
     * Метод для аутентификации пользователя.
     *
     * @param userSignInDto DTO с данными для входа пользователя.
     * @return объект класса {@link UserProfileAndTokenDto} с данными профиля пользователя и
     * сгенерированным JWT-токеном.
     * @throws UnauthorizedException в случае, если заданы неверные данные для входа.
     */
    UserProfileAndTokenDto userSignIn(UserSignInDto userSignInDto);

    /**
     * Метод для получения списка пользователей с учетом фильтрации, сортировки и постраничной навигации.
     *
     * @param paginationDto DTO объект, содержащий информацию о постраничной навигации, фильтрации и сортировке.
     * @return объект класса {@link UsersPageListDto}, содержащий список пользователей, соответствующий запросу и
     * информацию о постраничной навигации, фильтрации и сортировке.
     */
    UsersPageListDto getUserList(PaginationDto paginationDto);

    /**
     * Метод для получения информации о пользователе по его логину.
     *
     * @param login логин пользователя.
     * @return объект класса {@link UserProfileDto}, содержащий информацию о пользователе с указанным логином.
     * @throws NotFoundException если пользователь с указанным логином не найден.
     */
    UserProfileDto getUserInfo(String login);

    /**
     * Метод для получения информации о профиле текущего пользователя.
     *
     * @return объект класса {@link UserProfileDto} с информацией о профиле текущего пользователя.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     */
    UserProfileDto viewYourProfile();

    /**
     * Метод для обновления информации о профиле текущего пользователя.
     *
     * @param updateUserInfoDto объект класса {@link UpdateUserInfoDto} с обновленными данными пользователя.
     * @return объект класса {@link UserProfileDto} с обновленной информацией о профиле текущего пользователя.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     * @throws BadRequestException если дата рождения позже текущей даты.
     */
    UserProfileDto updateUserInfo(UpdateUserInfoDto updateUserInfoDto);

}
