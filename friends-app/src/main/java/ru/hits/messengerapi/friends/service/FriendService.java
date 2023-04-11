package ru.hits.messengerapi.friends.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.friends.dto.FriendDto;
import ru.hits.messengerapi.friends.repository.FriendRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;


}
