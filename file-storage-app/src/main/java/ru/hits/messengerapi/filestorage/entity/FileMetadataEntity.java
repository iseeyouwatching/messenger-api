package ru.hits.messengerapi.filestorage.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "file_metadata")
public class FileMetadataEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "filename")
    private String filename;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "object_name")
    private UUID objectName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    private String size;

}
