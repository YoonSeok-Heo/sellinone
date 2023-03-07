package com.wsb.sellinone.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String username;

    private String email;

    private String name;

    private String token;

}
