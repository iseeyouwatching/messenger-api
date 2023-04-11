package ru.hits.messengerapi.friends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.hits.messengerapi.common.EnableJWTUtil;

@EnableJWTUtil
@SpringBootApplication
public class FriendsServer {

	public static void main(String[] args) {
		SpringApplication.run(FriendsServer.class, args);
	}

}
