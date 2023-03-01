package com.wsb.sellinone.service.user;


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
       UserEntity userEntity = userRepository.findByUsername(request.getUsernaeme()).orElseThrow(() ->
               new BadCredentialsException("잘못된 계정정보입니다."));

       if(!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())){
           throw new BadCredentialsException("잘못된 계정정보입니다.");
       }

       return SignResponseDto.builder()
               .usernaeme(userEntity.getUsernaeme())
               .name(userEntity.getName())
               .email(userEntity.getEmail())
               .phone(userEntity.getPhone())
               .build();
   }

   public boolean join(SignRequestDto request) throws Exception {
       try{
           UserEntity userEntity = UserEntity.builder()
                   .usernaeme(request.getUsernaeme())
                   .password(request.getPassword())
                   .phone(request.getPhone())
                   .email(request.getEmail())
                   .name(request.getName())
                   .joinDate(LocalDateTime.parse(Utils.retDate(Utils.SEND_TIME)))
                   .lastModifiedDate(LocalDateTime.parse(Utils.retDate(Utils.SEND_TIME)))
                   .build();

           userEntity.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

           userRepository.save(userEntity);
       }catch (Exception e){
           log.error("join: " + e.getMessage());
           throw new Exception("잘못된 요청입니다.");
       }
       return true;
   }
}
