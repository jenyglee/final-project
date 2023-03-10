package com.project.sparta.chat.dto;

import com.sun.istack.NotNull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @desc 는 문자 채팅 방을 위한 dto 클래
 * */
// Stomp 를 통해 pub/sub 를 사용하면 구독자 관리가 알아서 된다!!
// 따라서 따로 세션 관리를 하는 코드를 작성할 필도 없고,
// 메시지를 다른 세션의 클라이언트에게 발송하는 것도 구현 필요가 없다!
@Data
@NoArgsConstructor
public class ChatRoomDto {
    @NotNull
    private String roomId; // 채팅방 아이디
    private String hostName;    //채팅방 만든 사람
    private String roomName; // 채팅방 이름 
    private int userCount; // 채팅방 인원수
    private int maxUserCnt; // 채팅방 최대 인원 제한

    public enum ChatType{  // 화상 채팅, 문자 채팅
        MSG, RTC
    }
    private ChatType chatType; //  채팅 타입 여부
    // ChatRoomDto 클래스는 하나로 가되 서비스를 나누었음
    public ConcurrentMap<String, String> userList = new ConcurrentHashMap<>();

    @Builder
    public ChatRoomDto(String roomId, String hostName, String roomName, int userCount,
        int maxUserCnt,
        ChatType chatType, ConcurrentMap<String, String> userList) {
        this.roomId = roomId;
        this.hostName = hostName;
        this.roomName = roomName;
        this.userCount = userCount;
        this.maxUserCnt = maxUserCnt;
        this.chatType = chatType;
        this.userList = userList;
    }
}
