package com.project.sparta.communityBoard.dto;


import com.project.sparta.communityBoard.entity.CommunityTag;
import com.project.sparta.communityComment.dto.CommentResponseDto;
import com.project.sparta.hashtag.entity.Hashtag;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityBoardOneResponseDto {
    private String title;
    private String nickName;
    private String contents;
    private Long likeCount;
    private List<String> imgList = new ArrayList<>();
    private List<Hashtag> tagList = new ArrayList<>();
    private List<CommentResponseDto> commentList = new ArrayList<>();
    private Boolean isLike;

    private String chatStatus;

    private int chatMemCnt;

    @Builder
    public CommunityBoardOneResponseDto(String title, String nickName, String contents,
        Long likeCount,
        List<String> imgList, List<Hashtag> tagList, List<CommentResponseDto> commentList,
        Boolean isLike, String chatStatus, int chatMemCnt) {
        this.title = title;
        this.nickName = nickName;
        this.contents = contents;
        this.likeCount = likeCount;
        this.imgList = imgList;
        this.tagList = tagList;
        this.commentList = commentList;
        this.isLike = isLike;
        this.chatStatus = chatStatus;
        this.chatMemCnt = chatMemCnt;
    }
}
