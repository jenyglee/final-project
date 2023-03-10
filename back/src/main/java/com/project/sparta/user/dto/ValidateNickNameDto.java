package com.project.sparta.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidateNickNameDto {

    @ApiModelProperty(example = "초보등산꾼")
    private String nickName;

}
