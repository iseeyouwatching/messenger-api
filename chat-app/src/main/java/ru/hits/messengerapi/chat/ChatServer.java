package ru.hits.messengerapi.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.hits.messengerapi.common.EnableCommonClasses;

@EnableCommonClasses
@SpringBootApplication
public class ChatServer
{
    public static void main(String[] args) {
        SpringApplication.run(ChatServer.class, args);
    }

}
