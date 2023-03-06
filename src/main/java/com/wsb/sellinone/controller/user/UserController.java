package com.wsb.sellinone.controller.user;

import com.wsb.sellinone.common.ApiResponse;
import com.wsb.sellinone.dto.user.JoinRequestDto;
import com.wsb.sellinone.dto.user.SignRequestDto;
import com.wsb.sellinone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<ApiResponse> join (@RequestBody JoinRequestDto joinRequestDto) throws Exception {
        log.info("UserController join : {}", joinRequestDto.toString());
        ApiResponse apiResponse = userService.join(joinRequestDto);

        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }
}
