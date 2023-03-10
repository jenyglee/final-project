package com.project.sparta.alarm.repository;

import com.project.sparta.alarm.entity.Alarm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRespository extends JpaRepository<Alarm, Long>{

    @Query("select a from Alarm a where a.userId=:userId and a.writerNickName<>:userNickName order by a.createAt desc")
    Page<Alarm> findMyAlarmList(@Param("userId")Long userId, Pageable pageable, @Param("userNickName")String userNickName);

    Optional<Alarm> findByIdAndUserId(Long alarmId, Long userId);

    @Query("select a from Alarm  a where a.boardId=:boardId")
    ArrayList<Alarm> selectAlaram(@Param("boardId") Long boardId);

    ArrayList<Alarm> findByBoardId(Long boardId);
}
