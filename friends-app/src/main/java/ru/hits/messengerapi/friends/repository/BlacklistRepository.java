package ru.hits.messengerapi.friends.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link BlacklistEntity}.
 */
@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistEntity, UUID> {

    /**
     * Найти сущность в черном списке по id заблокировавшего пользователя и id пользователя, которого заблокировали.
     *
     * @param targetUserId id заблокировавшего пользователя.
     * @param blockedUserId id пользователя, которого заблокировали.
     * @return найденная сущность из черного списка или пустой объект класса {@link Optional}.
     */
    Optional<BlacklistEntity> findByTargetUserIdAndBlockedUserId(UUID targetUserId, UUID blockedUserId);

    /**
     * Получить все сущности из черного списка по id целевого пользователя и дате удаления.
     *
     * @param targetUserId id пользователя, для которого ищем сущности в черном списке.
     * @param deletedDate дата удаления пользователя из черного списка.
     * @param pageable объект класса {@link Pageable}, содержащий информацию о странице.
     * @return список сущностей заблокированных пользователей, удовлетворяющих условиям поиска.
     */
    List<BlacklistEntity> findAllByTargetUserIdAndDeletedDate(UUID targetUserId,
                                                           LocalDateTime deletedDate,
                                                           Pageable pageable);

    /**
     * Получить все сущности из черного списка для целевого пользователя по его id,
     * фильтру с именем пользователя и дате удаления.
     *
     * @param targetUserId id пользователя, для которого ищем сущности в черном списке.
     * @param wildcardFullNameFilter строка-фильтр с именем пользователя.
     * @param deletedDate дата удаления записи из черного списка.
     * @param pageable объект класса {@link Pageable}, содержащий информацию о странице.
     * @return сущности из черного списка, удовлетворяющих условиям поиска.
     */
    List<BlacklistEntity> findAllByTargetUserIdAndBlockedUserNameLikeAndDeletedDate(UUID targetUserId,
                                                                            String wildcardFullNameFilter,
                                                                            LocalDateTime deletedDate,
                                                                            Pageable pageable);

    /**
     * Получить все сущности из черного списка по id заблокированного пользователя.
     *
     * @param blockedUserId id пользователя, который был заблокирован.
     * @return сущности из черного списка, удовлетворяющих условиям поиска.
     */
    List<BlacklistEntity> findAllByBlockedUserId(UUID blockedUserId);

}
