package com.project.sparta.communityBoard.controller;

import com.project.sparta.common.dto.PageResponseDto;
import com.project.sparta.communityBoard.dto.CommunityBoardRequestDto;
import com.project.sparta.communityBoard.dto.CommunityBoardResponseDto;
import com.project.sparta.communityBoard.entity.CommunityBoard;
import com.project.sparta.communityBoard.service.CommunityBoardService;
//import com.project.sparta.communityComment.controller.CommunityCommnetController;
import com.project.sparta.communityComment.dto.CommunityRequestDto;
import com.project.sparta.communityComment.dto.CommunityResponseDto;
import com.project.sparta.communityComment.entity.CommunityComment;
import com.project.sparta.communityComment.service.CommunityCommentService;
import com.project.sparta.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommunityBoardController {
  private final CommunityBoardService communityBoardService;
  @PostMapping("/community_boards")
  public ResponseEntity createCommunityBoard(@RequestBody CommunityBoardRequestDto communityBoardRequestDto
      , @AuthenticationPrincipal UserDetailsImpl userDetail) {
    CommunityBoardResponseDto communityBoardResponseDto = communityBoardService.createCommunityBoard(
        communityBoardRequestDto, userDetail.getUser());
    return new ResponseEntity<>(communityBoardResponseDto, HttpStatus.OK);
  }
  @GetMapping("/community_boards/{community_board_id}")
  public ResponseEntity getCommunityBoard(@PathVariable Long community_board_id) {
    CommunityBoardResponseDto communityBoardResponseDto = communityBoardService.getCommunityBoard(community_board_id);
    return new ResponseEntity<>(communityBoardResponseDto,HttpStatus.OK);
  }
  @GetMapping("/community_boards")
  public ResponseEntity getAllCommunityBoard(
      @RequestParam("page") int page,
      @RequestParam("size") int size) {

    List<CommunityBoardResponseDto> communityBoardResponseDto = communityBoardService.getAllCommunityBoard(page,size);
    return new ResponseEntity<>(communityBoardResponseDto,HttpStatus.OK);
  }
  @GetMapping("/community_boards/me_boards")
  public ResponseEntity getMyBoardAll(
      @RequestParam("page") int page,
      @RequestParam("size") int size,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    List<CommunityBoardResponseDto> communityBoardResponseDto = communityBoardService.getMyCommunityBoard(page,size,userDetails.getUser());
  return new ResponseEntity<>(communityBoardResponseDto,HttpStatus.OK);
  }

  @PatchMapping("/community_boards/{community_board_id}")
  public ResponseEntity updateCommunityBoard(@PathVariable Long community_board_id, @RequestBody CommunityBoardRequestDto communityBoardRequestDto
      ,@AuthenticationPrincipal UserDetailsImpl userDetail) {
    CommunityBoardResponseDto communityBoardResponseDto = communityBoardService.updateCommunityBoard(
        community_board_id, communityBoardRequestDto, userDetail.getUser());
    return new ResponseEntity<>(communityBoardResponseDto, HttpStatus.OK);
  }

  //선택한 게시글 삭제
  @DeleteMapping("/community_boards/{community_board_id}")
  public ResponseEntity deleteCommunityBoard(@PathVariable Long community_board_id) {
    communityBoardService.deleteCommunityBoard(community_board_id);
    return new ResponseEntity("보드 삭제 완료", HttpStatus.OK);
  }

}
