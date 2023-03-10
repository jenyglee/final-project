package com.project.sparta.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmRequetDto {

    @ApiModelProperty(example = "스웨거로 메세지")
    private String message;
    @ApiModelProperty(example = "2")
    private Long boardId;
    @ApiModelProperty(example = "보드타입")
    private String boardType;

}
