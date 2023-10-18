package com.wsb.sellinone.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GetRoleResponseDto {

    private String username;
    private List<String> roles;
}
