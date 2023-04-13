package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.helpingservices.implementation.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.PaginationDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserInfoDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.friends.FriendInfoDto;
import ru.hits.messengerapi.friends.dto.friends.FriendsPageListDto;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.BlacklistRepository;
import ru.hits.messengerapi.friends.service.BlacklistServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlacklistService implements BlacklistServiceInterface {

    private final BlacklistRepository blacklistRepository;
    private final CheckPaginationInfoService checkPaginationInfoService;
    private final SecurityProps securityProps;

    @Override
    public BlockedUsersPageListDto getBlockedUsers(PaginationDto paginationDto) {
        int pageNumber = paginationDto.getPageInfo().getPageNumber();
        int pageSize = paginationDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        List<BlacklistEntity> blockedUsers;
        if (paginationDto.getFullNameFilter() == null || paginationDto.getFullNameFilter().isBlank()) {
            blockedUsers = blacklistRepository
                    .findAllByTargetUserIdAndDeletedDate(targetUserId, null, pageable);
        }
        else {
            String wildcardFullNameFilter = "%" + paginationDto.getFullNameFilter() + "%";
            blockedUsers = blacklistRepository.findAllByTargetUserIdAndBlockedUserNameLikeAndDeletedDate(
                    targetUserId, wildcardFullNameFilter, null, pageable);
        }

        List<BlockedUserInfoDto> blockedUserInfoDtos = new ArrayList<>();
        for (BlacklistEntity blockedUser : blockedUsers) {
            blockedUserInfoDtos.add(new BlockedUserInfoDto(blockedUser));
        }

        return new BlockedUsersPageListDto(
                blockedUserInfoDtos,
                paginationDto.getPageInfo(),
                paginationDto.getFullNameFilter()
        );
    }
}
