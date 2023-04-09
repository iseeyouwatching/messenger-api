package ru.hits.messengerapi.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageInfoDto {

    private int pageNumber;

    private int pageSize;

}
