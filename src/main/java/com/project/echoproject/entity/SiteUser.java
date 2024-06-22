package com.project.echoproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @Column(length = 50, nullable = false)
    private String userId;
    @Column(length = 50, nullable = false)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Column(length = 50, nullable = false)
    private String email;
    @Column(length = 50, nullable = false)
    private String phoneNum;
    @Column(length = 2, nullable = false)
    private String gender;
    @Column(length = 200)
    private String imgUrl;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Integer reportCnt=0;

    private Integer couponId;
    private boolean couponUse;

    @OneToMany(mappedBy = "siteUser", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeBoards;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = LocalDateTime.now();
    }

}
