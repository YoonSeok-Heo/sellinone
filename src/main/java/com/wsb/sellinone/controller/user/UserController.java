package com.wsb.sellinone.controller.user;

import com.wsb.sellinone.common.ApiResponse;
import com.wsb.sellinone.dto.user.AddRoleRequestDto;
import com.wsb.sellinone.dto.user.GetRoleRequestDto;
import com.wsb.sellinone.dto.user.JoinRequestDto;
import com.wsb.sellinone.dto.user.LoginRequestDto;
import com.wsb.sellinone.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/join")
    public ResponseEntity<ApiResponse> join (
            @RequestBody JoinRequestDto joinRequestDto
    ) {

        ApiResponse apiResponse = userService.join(joinRequestDto);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @RequestBody LoginRequestDto loginRequest
    ) {

        ApiResponse apiResponse = userService.login(loginRequest);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/role")
    public ResponseEntity<ApiResponse> getRole(
            @RequestBody GetRoleRequestDto getRoleRequestDto
    ) {

        ApiResponse apiResponse = userService.getRole(getRoleRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/role")
    public ResponseEntity<ApiResponse> addRole(
            HttpServletRequest request,
            @RequestBody AddRoleRequestDto addRoleRequestDto
            ) {

//        ApiResponse apiResponse = new ApiResponse();
        try {
            log.info("addRoleRequestDto: {}", addRoleRequestDto);
            log.info("Authorization: {}", request.getHeader("Authorization"));

            ApiResponse apiResponse
                    = userService.addRoles(request, addRoleRequestDto);

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> test(){

        return new ResponseEntity<ApiResponse>(new ApiResponse(), HttpStatus.OK);
    }

}
