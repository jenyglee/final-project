package com.project.sparta.user.dto;

import com.project.sparta.user.entity.StatusEnum;
import com.project.sparta.user.entity.UserGradeEnum;
import com.project.sparta.user.entity.UserRoleEnum;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserOneResponseDto {

    private Long id;

    private String nickName;

    private int age;

    private String email;

    private String phoneNumber;
    private String profileImage;

    private UserGradeEnum userGradeEnum;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private StatusEnum status;

    @QueryProjection
    @Builder
    public UserOneResponseDto(Long id, String nickName, int age, String email, String phoneNumber,
        String profileImage, UserGradeEnum userGradeEnum, LocalDateTime createdAt,
        LocalDateTime modifiedAt, StatusEnum status) {
        this.id = id;
        this.nickName = nickName;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.userGradeEnum = userGradeEnum;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.status = status;
    }

    @Builder
    public UserOneResponseDto(Long id, String nickName, int age, String email, String phoneNumber) {
        this.id = id;
        this.nickName = nickName;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
