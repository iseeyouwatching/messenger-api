package ru.hits.messengerapi.chat.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessageDto {

    @NotNull(message = "Идентификатор чата является обязательным к заполнению.")
    private UUID chatId;

    @NotBlank(message = "Текст сообщения является обязательным к заполнению.")
    @Size(max = 500, message = "Сообщение должно содержать не больше 500 символов.")
    private String messageText;

    @Size(max = 10, message = "Количество файлов может быть максимум 10.")
    private List<UUID> attachments;

}
