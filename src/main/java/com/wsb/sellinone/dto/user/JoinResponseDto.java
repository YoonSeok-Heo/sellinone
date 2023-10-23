package com.wsb.sellinone.dto.user;

import lombok.*;

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
