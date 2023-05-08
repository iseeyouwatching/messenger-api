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
public class DialogueMessageDto {

    @NotNull(message = "Идентификатор пользователя, которому отправляется сообщение, является обязательным к заполнению.")
    private UUID receiverId;

    @NotBlank(message = "Текст сообщения является обязательным к заполнению.")
    @Size(max = 500, message = "Сообщение должно содержать не больше 500 символов.")
    private String messageText;

    private List<UUID> attachments;

}
