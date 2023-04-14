package ru.hits.messengerapi.friends.dto.common;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Класс, представляющий DTO, содержащую информацию необходимую для добавления человека в друзья/черный список.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddPersonDto {

    /**
     * Идентификатор пользователя.
     */
    @NotNull(message = "ID добавляемого пользователя является обязательным к заполнению.")
    private UUID id;

    /**
     * ФИО пользователя.
     */
    @NotBlank(message = "ФИО пользователя не может быть пустым.")
    private String fullName;

}
