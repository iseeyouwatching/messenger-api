package ru.hits.messengerapi.filestorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import ru.hits.messengerapi.common.EnableCommonClasses;

@EnableCommonClasses
@SpringBootApplication
@ConfigurationPropertiesScan("ru.hits.messengerapi.filestorage")
public class FileStorageServer
{
    public static void main(String[] args) {
        SpringApplication.run(FileStorageServer.class, args);
    }

}
