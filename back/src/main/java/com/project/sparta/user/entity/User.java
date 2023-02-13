package com.project.sparta.user.entity;

import com.project.sparta.admin.entity.StatusEnum;
import com.project.sparta.communityBoard.entity.CommunityBoard;

import java.util.ArrayList;

import lombok.*;

import java.util.List;
import javax.persistence.*;

@Entity(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long Id;

    @Column(nullable = false)
    protected String nickName;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false, unique = true)
    protected String email;
    // @Column(nullable = false)
    private int age;

    // @Column(nullable = false)
    private String phoneNumber;

    // @Column(nullable = false)
    private String userImageUrl;

    @Enumerated(value = EnumType.STRING)
    protected UserGradeEnum gradeEnum;

    @Enumerated(value = EnumType.STRING)
    protected UserRoleEnum role;

    @Enumerated(value = EnumType.STRING)
    protected StatusEnum status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserTag> tags = new ArrayList<>();

    @Column
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<CommunityBoard> communityBoards = new ArrayList<>();

    @Column
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<CommunityBoard> recommendBoards = new ArrayList<>();

    private int enterCount;

    private int makeCount;

    public void updateUserTags(List<UserTag> userTagList){
        this.tags = userTagList;
    }

    @Builder(builderClassName = "user", builderMethodName = "userBuilder")
    public User(String email, String password, String nickName, int age, String phoneNumber, String userImageUrl) {
        this.password = password;
        this.nickName = nickName;
        this.email = email;
        this.role = UserRoleEnum.USER;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.userImageUrl = userImageUrl;
        this.gradeEnum = UserGradeEnum.MOUNTAIN_CHILDREN;
        this.status = StatusEnum.USER_REGISTERED;
        // this.tags = tags;
    }
    @Builder(builderClassName = "admin", builderMethodName = "adminBuilder")
    public User(String email, String password, String nickName) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.role = UserRoleEnum.ADMIN;
        this.status = StatusEnum.ADMIN_REGISTERED;
    }
}
