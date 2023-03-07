package com.wsb.sellinone.service.user;


import com.wsb.sellinone.common.ApiResponse;
import com.wsb.sellinone.common.Utils;
import com.wsb.sellinone.dto.user.*;
import com.wsb.sellinone.entity.user.Authority;
import com.wsb.sellinone.entity.user.UserEntity;
import com.wsb.sellinone.jwt.JwtProvider;
import com.wsb.sellinone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final JwtProvider jwtProvider;

   public ApiResponse login(LoginRequestDto loginRequest){

       ApiResponse apiResponse = null;

       UserEntity userEntity = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() ->
               new BadCredentialsException("잘못된 계정정보입니다."));

       if(!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())){
           throw new BadCredentialsException("잘못된 계정정보입니다.");
       }

       apiResponse = new ApiResponse(200, "로그인 성공");

       LoginResponseDto loginResponseDto = LoginResponseDto.builder()
               .username(userEntity.getUsername())
               .name(userEntity.getName())
               .email(userEntity.getEmail())
               .token(jwtProvider.createToken(userEntity.getUsername(), userEntity.getRoles()))
               .build();
       apiResponse.putData("data", loginResponseDto);

       return apiResponse;
   }

   @Transactional(propagation = Propagation.SUPPORTS)
   public ApiResponse join(JoinRequestDto request) {

       ApiResponse apiResponse = null;
       try{
           UserEntity userEntity = UserEntity.builder()
                   .username(request.getUsername())
                   .password(passwordEncoder.encode(request.getPassword()))
                   .phone(request.getPhone())
                   .email(request.getEmail())
                   .name(request.getName())
                   .joinDate(Utils.retDate(Utils.SEND_TIME))
                   .lastModifiedDate(Utils.retDate(Utils.SEND_TIME))
                   .build();

           userEntity.setRoles(Collections.singletonList(Authority.builder()
                   .name("ROLE_USER")
                   .build()));

           userRepository.save(userEntity);

           JoinResponseDto joinResponseDto = JoinResponseDto.builder()
                   .username(userEntity.getUsername())
                   .name(userEntity.getName())
                   .email(userEntity.getEmail())
                   .build();

           apiResponse = new ApiResponse(200, "회원 가입 성공");
           apiResponse.putData("data", joinResponseDto);

       }catch (Exception e){
           log.error("UserService join: " + e.getMessage());
           log.error("{}", e.getStackTrace());
           apiResponse = new ApiResponse(400, "회원 가입 실패");
       }
       return apiResponse;
   }
}
