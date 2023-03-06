package com.wsb.sellinone.dto.user;


import com.wsb.sellinone.entity.user.Authority;
import com.wsb.sellinone.entity.user.UserEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignResponseDto {

    private String username;

    private String phone;

    private String email;

    private String name;

    private List<Authority> roles = new ArrayList<>();

    private String token;

    public SignResponseDto(UserEntity user){
        this.username = user.getUsername();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.name = user.getName();
        this.roles = user.getRoles();
    }
}
