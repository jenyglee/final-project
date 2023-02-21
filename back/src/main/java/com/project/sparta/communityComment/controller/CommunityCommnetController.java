package com.project.sparta.communityComment.controller;

import com.project.sparta.communityComment.dto.CommunityRequestDto;
import com.project.sparta.communityComment.service.CommunityCommentService;
import com.project.sparta.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import com.project.sparta.communityComment.dto.CommentResponseDto;

@Api(tags = {"커뮤니티 보드 댓글 API"})
@RestController
@RequiredArgsConstructor
public class CommunityCommnetController {

  private final CommunityCommentService commentService;

  //커뮤니티 댓글 작성
  @ApiOperation(value = "커뮤니티 댓글 작성",response = Join.class)
  @PostMapping("/comments/communities/{boardId}")
  public ResponseEntity createCommunityComment(@PathVariable Long boardId
      , @RequestBody CommunityRequestDto communityRequestDto
      , @AuthenticationPrincipal UserDetailsImpl userDetail) {
    CommentResponseDto communityResponseDto = commentService.createCommunityComments(boardId,
        communityRequestDto, userDetail.getUser());
    return new ResponseEntity<>(communityResponseDto, HttpStatus.OK);
  }

  //커뮤니티 댓글 수정
  @ApiOperation(value = "커뮤니티 댓글 수정",response = Join.class)
  @PatchMapping("/communities/{boardId}/comments/{commentId}")
  public ResponseEntity updateCommunityComment(
      @PathVariable Long boardId,
      @PathVariable Long commentId,
      @RequestBody CommunityRequestDto communityRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetail) {
    commentService.updateCommunityComments(boardId,commentId,
        communityRequestDto, userDetail.getUser());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  //커뮤니티 댓글 삭제
  @ApiOperation(value = "커뮤니티 댓글 삭제",response = Join.class)
  @DeleteMapping("/comments/{comment_id}/communities/{boardId}")
  public ResponseEntity deleteCommunityComment(@PathVariable Long boardId,
      @PathVariable Long comment_id
      ,@AuthenticationPrincipal UserDetailsImpl userDetail) {
    commentService.deleteCommunityComments(boardId, comment_id,userDetail.getUser());
    return new ResponseEntity("댓글 삭제 완료", HttpStatus.OK);
  }


}
