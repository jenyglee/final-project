package com.project.sparta.communityComment.controller;

import com.project.sparta.communityComment.dto.CommunityRequestDto;
import com.project.sparta.communityComment.service.CommunityCommentService;
import com.project.sparta.security.UserDetailsImpl;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Join;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = {"댓글"})
@RestController
@RequiredArgsConstructor
public class CommunityCommnetController {

    private final CommunityCommentService commentService;


    //커뮤니티 댓글 작성
    @ApiOperation(value = "댓글 작성", response = Join.class)
    @PostMapping("/comments/communities/{boardId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "boardId", value = "게시글 ID", required = true, dataType = "Long", paramType = "path", example = "123"),
    })
    public ResponseEntity createCommunityComment(@PathVariable Long boardId
        , @RequestBody  @ApiParam(value = "커뮤니티 댓글 작성 값", required = true) CommunityRequestDto communityRequestDto
        , @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetail) {

        commentService.createCommunityComments(boardId, communityRequestDto, userDetail.getUser());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //커뮤니티 댓글 수정
    @ApiOperation(value = "커뮤니티 댓글 수정", response = Join.class)
    @PatchMapping("/comments/{commentId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "커뮤니티 댓글 ID", required = true, dataType = "Long", paramType = "path", example = "123"),
    })
    public ResponseEntity updateCommunityComment(
        @PathVariable Long commentId,
        @RequestBody @ApiParam(value = "커뮤니티 댓글 수정", required = true) CommunityRequestDto communityRequestDto,
        @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetail) {
        commentService.updateCommunityComments(commentId, communityRequestDto, userDetail.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //커뮤니티 댓글 삭제
    @ApiOperation(value = "커뮤니티 댓글 삭제", response = Join.class)
    @DeleteMapping("/comments/{commentId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "커뮤니티 댓글 ID", required = true, dataType = "Long", paramType = "path", example = "123"),
    })
    public ResponseEntity deleteCommunityComment(@PathVariable Long commentId,
        @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetail) {
        commentService.deleteCommunityComments(commentId, userDetail.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
