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
public class JoinResponseDto {

    private String username;

    private String email;

    private String name;

}
