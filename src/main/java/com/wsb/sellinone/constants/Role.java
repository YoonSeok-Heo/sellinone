package com.wsb.sellinone.constants;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

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

    @JsonCreator
    public static Role from(String roleName) {
        for (Role role : Role.values()) {
            if (role.getRoleName().equals(roleName)) {
                return role;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    @JsonValue
    public String getRoleName(){
        return roleName;
    }
}