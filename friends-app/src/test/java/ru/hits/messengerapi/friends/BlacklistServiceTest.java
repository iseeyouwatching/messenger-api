package ru.hits.messengerapi.friends;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.hits.messengerapi.common.dto.PageInfoDto;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;
import ru.hits.messengerapi.friends.repository.BlacklistRepository;
import ru.hits.messengerapi.friends.repository.FriendsRepository;
import ru.hits.messengerapi.friends.service.BlacklistService;
import ru.hits.messengerapi.friends.service.FriendsService;
import ru.hits.messengerapi.friends.service.IntegrationRequestsService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
public class BlacklistServiceTest {

    @Mock
    private BlacklistRepository blacklistRepository;

    @Mock
    private CheckPaginationInfoService checkPaginationInfoService;

    @Mock
    private IntegrationRequestsService integrationRequestsService;

    @Mock
    private FriendsService friendsService;

    @Mock
    private FriendsRepository friendsRepository;

    @InjectMocks
    private BlacklistService blacklistService;

    @Test
    public void getBlockedUsers_WithValidPaginationAndNoFullNameFilter_ReturnsBlockedUsersPageListDto() {
        PaginationWithFullNameFilterDto paginationWithFullNameFilterDto = new PaginationWithFullNameFilterDto(new PageInfoDto(null, null), null);
        paginationWithFullNameFilterDto.getPageInfo().setPageNumber(1);
        paginationWithFullNameFilterDto.getPageInfo().setPageSize(10);
        UUID targetUserId = UUID.randomUUID();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", targetUserId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        when(blacklistRepository.findAllByTargetUserIdAndDeletedDate(targetUserId, null, PageRequest.of(0, 10)))
                .thenReturn(List.of(new BlacklistEntity()));

        BlockedUsersPageListDto result = blacklistService.getBlockedUsers(paginationWithFullNameFilterDto);

        assertNotNull(result);
    }

    @Test
    public void isUserBlocked_WithValidBlockedUserId_ReturnsFalse() {
        UUID blockedUserId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", targetUserId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        when(blacklistRepository.findByTargetUserIdAndBlockedUserId(targetUserId, blockedUserId))
                .thenReturn(Optional.of(new BlacklistEntity()));

        boolean result = blacklistService.checkIfTheTargetUserBlacklisted(targetUserId, blockedUserId);

        assertFalse(result);
    }

}
