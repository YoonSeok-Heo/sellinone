package com.wsb.sellinone.service.user;

import com.wsb.sellinone.common.ApiResponse;
import com.wsb.sellinone.constants.Role;
import com.wsb.sellinone.dto.user.*;
import com.wsb.sellinone.entity.user.AuthorityEntity;
import com.wsb.sellinone.entity.user.UserEntity;
import com.wsb.sellinone.jwt.JwtProvider;
import com.wsb.sellinone.repository.user.AuthorityRepository;
import com.wsb.sellinone.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public ApiResponse login(LoginRequestDto loginRequest) {

        ApiResponse apiResponse;

        UserEntity userEntity
            = userRepository.findByUsername(
                loginRequest.getUsername()).orElseThrow(()
                    -> new BadCredentialsException(
                        HttpStatus.UNAUTHORIZED.getReasonPhrase()
        ));

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                userEntity.getPassword())
        ) {
            throw new BadCredentialsException(
                    HttpStatus.UNAUTHORIZED.getReasonPhrase()
            );
        }

        apiResponse = new ApiResponse(HttpStatus.OK.value(), "로그인 성공");
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .username(userEntity.getUsername())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .token(jwtProvider.createToken(
                        userEntity.getUsername(),
                        userEntity.getRoles())
                )
                .build();
        apiResponse.putData("data", loginResponseDto);

        return apiResponse;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public ApiResponse join(JoinRequestDto request) {

        ApiResponse apiResponse = new ApiResponse();

        try {
            UserEntity userEntity = UserEntity.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .name(request.getName())
                    .joinDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

            userEntity.setRoles(
                    Collections.singletonList(
                            AuthorityEntity.builder()
                                    .name(Role.ROLE_USER.getRoleName())
                                    .lastModifiedDate(LocalDateTime.now())
                                    .build()
                    )
            );

            userRepository.save(userEntity);

            JoinResponseDto joinResponseDto
                    = JoinResponseDto.builder()
                    .username(userEntity.getUsername())
                    .name(userEntity.getName())
                    .email(userEntity.getEmail())
                    .build();

            apiResponse = new ApiResponse(
                    HttpStatus.OK.value(),
                    "회원 가입 성공"
            );

            apiResponse.putData("data", joinResponseDto);

        } catch (DataIntegrityViolationException e) {
            log.error("UserService join(DataIntegrityViolationException): {} \n{} ",
                    e.getMessage(),
                    e.getStackTrace()
            );

            apiResponse = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "회원 가입 실패"
            );

        } catch (Exception e){
            log.error("UserService join(Exception): {} \n{} ",
                    e.getMessage(),
                    e.getStackTrace()
            );

            apiResponse = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "회원 가입 실패"
            );
        } finally {
            return apiResponse;
        }
    }

    public ApiResponse getRole(GetRoleRequestDto getRoleRequestDto) {

        ApiResponse apiResponse = new ApiResponse();
        return apiResponse;
    }

    public ApiResponse addRoles(
            HttpServletRequest request,
            AddRoleRequestDto addRoleRequestDto
    ) throws Exception {
        ApiResponse apiResponse = null;
        try {
            apiResponse = new ApiResponse();
            String token = jwtProvider.resolveToken(request);
            String removePrefixToken = jwtProvider.removeTokenPrefix(token);
            log.info("removeprefixToken: {}", removePrefixToken);

            String username = jwtProvider.getUsername(removePrefixToken);
            log.info("username: {}", username);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = (UsernamePasswordAuthenticationToken) jwtProvider.getAuthentication(removePrefixToken);
            log.info("userAuth: {}", jwtProvider.getAuthentication(removePrefixToken));

            log.info("auth: {}:", usernamePasswordAuthenticationToken.getAuthorities());
            log.info("toArray: {}", usernamePasswordAuthenticationToken.getAuthorities().stream().toList());

            List<AuthorityEntity> authorityEntityList = new ArrayList<>();
            for (Role role : addRoleRequestDto.getRoles()) {
                if (null == role){
                    continue;
                }
                UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
                AuthorityEntity authorityEntity = AuthorityEntity.builder()
                        .username(userEntity)
                        .name(role.getRoleName())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                authorityEntityList.add(authorityEntity);
            }

            log.info("{}", authorityEntityList);
            authorityRepository.saveAll(authorityEntityList);

            log.info("pause");
        } catch (Exception e) {
            log.error("{}\n{}", e.getMessage(), e.getStackTrace());
        }

        return apiResponse;
    }
}
