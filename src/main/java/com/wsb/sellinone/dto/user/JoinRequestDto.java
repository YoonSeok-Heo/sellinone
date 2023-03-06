package com.wsb.sellinone.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JoinRequestDto {

    private String username;

    private String password;

    private String phone;

    private String email;

    private String name;

}
