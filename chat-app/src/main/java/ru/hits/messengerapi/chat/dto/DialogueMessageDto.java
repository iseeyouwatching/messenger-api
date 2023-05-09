package ru.hits.messengerapi.chat.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/**
 * DTO, содержащая информацию о сообщении, которое отправляется в диалог.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DialogueMessageDto {

    /**
     * Уникальный идентификатор получателя.
     */
    @NotNull(message = "Идентификатор пользователя, которому отправляется сообщение, " +
            "является обязательным к заполнению.")
    private UUID receiverId;

    /**
     * Текст сообщения.
     */
    @NotBlank(message = "Текст сообщения является обязательным к заполнению.")
    @Size(max = 500, message = "Сообщение должно содержать не больше 500 символов.")
    private String messageText;

    /**
     * Список уникальных идентификаторов вложений, которые прикрепляются к сообщению.
     */
    @Size(max = 10, message = "Количество файлов может быть максимум 10.")
    private List<UUID> attachments;

}
