package ru.hits.messengerapi.filestorage.config;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Конфигурация MinIO.
 */
@ToString
@Getter
@Setter
@ConfigurationProperties("minio")
public class MinioConfiguration {

    /**
     * URL сервера MinIO.
     */
    private String url;

    /**
     * Ключ доступа для аутентификации в MinIO.
     */
    private String accessKey;

    /**
     * Секретный ключ для аутентификации в MinIO.
     */
    private String secretKey;

    /**
     * Имя контейнера (bucket) в MinIO.
     */
    private String bucket;

    /**
     * Создает и настраивает клиент MinIO.
     *
     * @return экземпляр {@link MinioClient}.
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .credentials(accessKey, secretKey)
                .endpoint(url)
                .build();
    }

}
