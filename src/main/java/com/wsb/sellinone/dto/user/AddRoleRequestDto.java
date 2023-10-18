package com.wsb.sellinone.dto.user;

import com.wsb.sellinone.constants.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AddRoleRequestDto {

    private List<Role> roles;

}
