package ru.hits.messengerapi.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.hits.messengerapi.common.EnableExceptionHandlerController;

@SpringBootApplication
@EnableExceptionHandlerController
public class UserServer {

	public static void main(String[] args) {
		SpringApplication.run(UserServer.class, args);
	}

}
