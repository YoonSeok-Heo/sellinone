package com.wsb.sellinone.constants;


import lombok.Getter;

@Getter
public enum Role {

    ROLE_ADMIN(0, "ROLE_ADMIN"),
    ROLE_USER(100, "ROLE_USER"),
    ROLE_COU(200, "ROLE_COU");

    Role(int code, String roleName){
        this.code = code;
        this.roleName = roleName;
    }

    private final int code;
    private final String roleName;
}
