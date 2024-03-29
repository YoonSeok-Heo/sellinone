package com.wsb.sellinone.controller.admin;

import com.wsb.sellinone.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("")
    public ResponseEntity<ApiResponse> test(){

        return new ResponseEntity(new ApiResponse(), HttpStatus.OK);
    }

}
