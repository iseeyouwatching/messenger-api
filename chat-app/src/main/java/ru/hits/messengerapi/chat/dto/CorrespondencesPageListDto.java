package ru.hits.messengerapi.chat.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrespondencesPageListDto {

    private List<CorrespondenceDto> correspondences;

    private PageInfoDto pageInfo;

    private String chatNameFilter;

}
