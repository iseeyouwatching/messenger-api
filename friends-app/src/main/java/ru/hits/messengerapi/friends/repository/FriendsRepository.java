package ru.hits.messengerapi.friends.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.friends.entity.FriendEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link FriendEntity}.
 */
@Repository
public interface FriendsRepository extends JpaRepository<FriendEntity, UUID> {

    /**
     * Найти друзей целевого пользователя по его id и id друга.
     *
     * @param targetUserId id целевого пользователя.
     * @param addedUserId id друга.
     * @return найденная сущность из друзей или пустой объект класса {@link Optional}.
     */
    Optional<FriendEntity> findByTargetUserIdAndAddedUserId(UUID targetUserId, UUID addedUserId);

    /**
     * Найти друзей целевого пользователя по его id и дате удаления из друзей.
     *
     * @param targetUserId id целевого пользователя.
     * @param deletedDate дата удаления из друзей.
     * @param pageable объект класса {@link Pageable}, содержащий информацию о странице.
     * @return список сущностей друзей, удовлетворяющих условиям поиска.
     */
    List<FriendEntity> findAllByTargetUserIdAndDeletedDate(UUID targetUserId,
                                                           LocalDateTime deletedDate,
                                                           Pageable pageable);

    /**
     * Найти друзей целевого пользователя по его id, у которых имя совпадает с шаблоном
     * и дата удаления равна указанной.
     *
     * @param targetUserId id целевого пользователя.
     * @param wildcardFullNameFilter строка-фильтр с именем пользователя.
     * @param deletedDate дата удаления из друзей.
     * @param pageable объект класса {@link Pageable}, содержащий информацию о странице.
     * @return список сущностей друзей, удовлетворяющих условиям поиска.
     */
    List<FriendEntity> findAllByTargetUserIdAndFriendNameLikeAndDeletedDate(UUID targetUserId,
                                                                            String wildcardFullNameFilter,
                                                                            LocalDateTime deletedDate,
                                                                            Pageable pageable);

    /**
     * Найти сущности друзей по id добавленного пользователя.
     *
     * @param addedUserId id пользователя, который был добавлен в друзья.
     * @return список сущностей друзей, удовлетворяющих условиям поиска.
     */
    List<FriendEntity> findAllByAddedUserId(UUID addedUserId);

    List<FriendEntity> findAllByTargetUserId(UUID targetUserId);

}
