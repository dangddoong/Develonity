package com.develonity.user.controller;

import com.develonity.common.jwt.JwtUtil;
import com.develonity.common.security.users.UserDetailsImpl;
import com.develonity.user.dto.LoginRequest;
import com.develonity.user.dto.ProfileResponse;
import com.develonity.user.dto.RegisterRequest;
import com.develonity.user.dto.TokenResponse;
import com.develonity.user.dto.WithdrawalRequest;
import com.develonity.user.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

  private final UserService userService;

  @PostMapping("/register") //회원가입
  public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
    userService.register(registerRequest);
    return new ResponseEntity<>("회원가입성공", HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public TokenResponse login(@RequestBody LoginRequest loginRequest,
      HttpServletResponse httpServletResponse) {
    TokenResponse tokenResponse = userService.login(loginRequest);
    // 44라인은 일단 포스트맨 테스트의 용이성을 위해 넣어두었습니다. 추후 프론트 구현 이후에는 삭제 예정입니다.
    httpServletResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER, tokenResponse.getAccessToken());
    return tokenResponse;
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    userService.logout(userDetails.getUser().getLoginId());
    return new ResponseEntity<>("로그아웃 성공", HttpStatus.OK);
  }

  @PatchMapping("/withdrawal")
  public ResponseEntity<String> withdrawal(@RequestBody WithdrawalRequest withdrawalRequest,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    userService.withdrawal(userDetails.getUsername(), withdrawalRequest.getPassword());
    return new ResponseEntity<>("회원탈퇴 성공", HttpStatus.OK);
  }

  @PostMapping("/reissue")
  public TokenResponse reissue(HttpServletRequest httpServletRequest) {
    String refreshToken = JwtUtil.resolveRefreshToken(httpServletRequest);
    return userService.reissue(refreshToken);
  }

  // 내 프로필조회
  @GetMapping("/users/me/profile")
  public ProfileResponse getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return userService.getProfile(userDetails.getUserId());
  }

  //  타인 프로필조회
  @GetMapping("/users/{userId}/profile")
  public ProfileResponse getUserProfile(@PathVariable Long userId) {
    return userService.getProfile(userId);
  }

  //내 프로필 정보 수정 (닉네임, 프로필사진)
//  @PatchMapping("/users/me/profile")
//  public
//
//  // 개인정보 조회 (이름, 비밀번호, 이메일, 핸드폰번호, 주소)
//  @GetMapping("/users/me/personal-information")
//
//  // 개인정보 수정 (이름, 비밀번호, 이메일, 핸드폰번호, 주소)
//  @PutMapping("/users/me/personal-information")

  // ----아래부터는 애매한 부분 ---
  //게시글 스크랩 저장 ?-? (이거는 애매함)
  // 스크랩 게시물 전체 조회
  // 내 주문 정보 확인(이거는 오더에서?)
  // 본인 글 전체조회 (그럼 이건 보드에서)
  // 본인 댓글 전체조회


}
