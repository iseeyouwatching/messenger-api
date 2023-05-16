package ru.hits.messengerapi.filestorage.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сущность, представляющая метаданные файла.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "file_metadata")
public class FileMetadataEntity {

    /**
     * Уникальный идентификатор сущности.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    /**
     * Имя файла.
     */
    @Column(name = "filename")
    private String filename;

    /**
     * Имя контейнера (bucket), в котором находится файл.
     */
    @Column(name = "bucket")
    private String bucket;

    /**
     * Уникальное имя объекта файла.
     */
    @Column(name = "object_name")
    private UUID objectName;

    /**
     * Тип содержимого файла (content type).
     */
    @Column(name = "content_type")
    private String contentType;

    /**
     * Дата создания файла.
     */
    @Column(name = "creation_date")
    private LocalDate creationDate;

    /**
     * Размер файла в MB.
     */
    private String size;

}
