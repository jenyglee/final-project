package com.project.sparta.communityBoard.repository;

import com.project.sparta.communityBoard.entity.CommunityBoard;
import java.util.Optional;

import com.project.sparta.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<CommunityBoard, Long>, BoardRepositoryCustom {
  Page<CommunityBoard> findById(Pageable pageable,Long Id);
  Page<CommunityBoard> findAll(Pageable pageable);
  Optional<CommunityBoard> findByIdAndUser_NickName(Long id, String nickName);


  @Query("select count(cboard) from CommunityBoard cboard where cboard.user.Id=:userId")
  Long countMyCommunity(@Param("userId") Long userId);

  @Query("select count(cboard) from CommunityBoard cboard where cboard.user.Id=:userId and cboard.chatStatus='Y'")
  int countMyChat(@Param("userId") Long userId);

  @Query("delete from CommunityBoard cb where cb.id =:boardId")
  @Modifying
  void deleteById(@Param("boardId") Long boardId);
}
