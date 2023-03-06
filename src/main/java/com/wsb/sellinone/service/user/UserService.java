package com.wsb.sellinone.service.user;


import com.wsb.sellinone.common.ApiResponse;
import com.wsb.sellinone.common.Utils;
import com.wsb.sellinone.dto.user.SignRequestDto;
import com.wsb.sellinone.dto.user.SignResponseDto;
import com.wsb.sellinone.entity.user.Authority;
import com.wsb.sellinone.entity.user.UserEntity;
import com.wsb.sellinone.jwt.JwtProvider;
import com.wsb.sellinone.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final JwtProvider jwtProvider;

   public SignResponseDto login(SignRequestDto request){
       UserEntity userEntity = userRepository.findByUsername(request.getUsername()).orElseThrow(() ->
               new BadCredentialsException("잘못된 계정정보입니다."));

       if(!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())){
           throw new BadCredentialsException("잘못된 계정정보입니다.");
       }

       return SignResponseDto.builder()
               .username(userEntity.getUsername())
               .name(userEntity.getName())
               .email(userEntity.getEmail())
               .phone(userEntity.getPhone())
               .build();
   }

   public ApiResponse join(SignRequestDto request) {

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

           SignResponseDto signResponseDto = SignResponseDto.builder()
                   .username(userEntity.getUsername())
                   .name(userEntity.getName())
                   .email(userEntity.getEmail())
                   .build();

           apiResponse = new ApiResponse(200, "회원 가입 성공");
           apiResponse.putData("data", signResponseDto);

       }catch (Exception e){
           log.error("UserService join: " + e.getMessage());
           log.error("{}", e.getStackTrace());
           apiResponse = new ApiResponse(400, "회원 가입 실패");
       }
       return apiResponse;
   }
}
