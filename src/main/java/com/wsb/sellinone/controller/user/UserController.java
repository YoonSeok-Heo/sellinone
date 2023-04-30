package com.wsb.sellinone.controller.user;

import com.wsb.sellinone.common.ApiResponse;
import com.wsb.sellinone.dto.user.JoinRequestDto;
import com.wsb.sellinone.dto.user.LoginRequestDto;
import com.wsb.sellinone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<ApiResponse> join (@RequestBody JoinRequestDto joinRequestDto) {
        log.info("UserController join : {}", joinRequestDto.toString());
        ApiResponse apiResponse = userService.join(joinRequestDto);

        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDto loginRequest){
        log.info("UserController login : {}", loginRequest.toString());
        ApiResponse apiResponse = userService.login(loginRequest);

        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> test(){

        return new ResponseEntity<ApiResponse>(new ApiResponse(), HttpStatus.OK);
    }

}
