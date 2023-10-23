package com.wsb.sellinone.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name="authority")
@IdClass(AuthorityKey.class)
@Table(indexes = {
        @Index(name = "idx_username", columnList = "username")
})
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorityEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @JsonIgnore
//    private Long id;

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private String name;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    @JsonIgnore
    private UserEntity username;

    public void setMember(UserEntity userEntity) {
        this.username = userEntity;
    }

    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;
}