package com.project.sparta.recommendCourse.entity;

import com.project.sparta.admin.entity.Timestamped;
import com.project.sparta.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendCoursePost extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommandCourse_post_id")
    private Long Id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int score;
    @Column
    private String season;
    @Column(nullable = false)
    private int altitude;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private PostStatusEnum postStatus;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "recommendCoursePost",cascade = CascadeType.PERSIST)
    private List<RecommendCourseImg> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RecommendCoursePost(String title, String contents , int score, String season, int altitude, Long userId) {
        this.title = title;
        this.contents = contents;
        this.season = season;
        this.score = score;
        this.altitude = altitude;
        this.postStatus = PostStatusEnum.VAILABLE;
        this.userId = userId;
    }

    public void modifyOfferCousePost(String title,String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void statusModifyOfferCousePost(PostStatusEnum postStatus){
        this.postStatus = postStatus;
    }
}