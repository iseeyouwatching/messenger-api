package ru.hits.messengerapi.user;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.hits.messengerapi.common.EnableExceptionHandlerController;

@SpringBootApplication
@EnableExceptionHandlerController
public class UserServer {

	public static void main(String[] args) {
		SpringApplication.run(UserServer.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
