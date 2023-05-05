package ru.hits.messengerapi.notifications;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.hits.messengerapi.common.EnableCommonClasses;

@EnableCommonClasses
@SpringBootApplication
public class NotificationsServer
{
    public static void main(String[] args) {
        SpringApplication.run(NotificationsServer.class, args);
    }

}
