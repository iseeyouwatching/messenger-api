package ru.hits.messengerapi.filestorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.filestorage.entity.FileMetadataEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с метаданными файлов.
 */
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadataEntity, UUID> {

    /**
     * Поиск метаданных файла по уникальному имени объекта.
     *
     * @param id уникальное имя объекта файла
     * @return объект Optional, содержащий найденные метаданные файла, если они существуют
     */
    Optional<FileMetadataEntity> findByObjectName(UUID id);

}
