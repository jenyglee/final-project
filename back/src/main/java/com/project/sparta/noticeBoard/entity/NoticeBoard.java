package com.project.sparta.noticeBoard.entity;

import com.project.sparta.common.entity.Timestamped;
import com.project.sparta.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@NoArgsConstructor
@Getter
public class NoticeBoard extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="notice_board_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    private User user;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private NoticeCategoryEnum category;

    @Builder
    public NoticeBoard(User user, String title, String contents, NoticeCategoryEnum category) {
        //영속성 컨텍스트에 있는 user 여야 함
        this.user = user;
        this.title = title;
        this.contents = contents;
        this.category = category;
    }

    public void update(String title, String contents, NoticeCategoryEnum category){
        this.title = title;
        this.contents = contents;
        this.category = category;
    }

}
