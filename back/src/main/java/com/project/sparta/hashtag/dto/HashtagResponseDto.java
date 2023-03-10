package com.project.sparta.hashtag.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HashtagResponseDto {
    private Long id;
    private String name;

    public HashtagResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
