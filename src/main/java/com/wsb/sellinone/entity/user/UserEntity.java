package com.wsb.sellinone.entity.user;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="user")
public class UserEntity implements Persistable<String> {

    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "username", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Authority> roles = new ArrayList<>();

    public void setRoles(List<Authority> role){
        this.roles = role;
        role.forEach(o -> o.setMember(this));
    }

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

    @Column
    private String serviceLife;

    @Column
    private String couAccessKey;

    @Column
    private String couSecretKey;

    @Override
    public String getId() {
        return username;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
