package com.develonity.user.dto;

import com.develonity.user.entity.User;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class RegisterRequest {

  @Size(min = 6, max = 15)
  @Pattern(regexp = "^[a-z0-9]*$", message = "영어 소문자와 숫자만 가능합니다.")
  private final String loginId;
  //  @Size(min = 6, max = 15)
  @Pattern(regexp = "^.*(?=^.{8,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$", message = "비밀번호는 8~15자리 숫자, 문자, 특수문자를 모두 포함해야합니다.")
  private final String password;
  private final String realName;
  @Size(min = 2, max = 10)
  private final String nickname;
  private final String profileImageUrl;
  @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "이메일 형식을 확인해주세요")
  private final String email;
  private final String phoneNumber;
  private final String detailAddress;
  @Size(min = 5, max = 5)
  private final String zipcode;

  public User toEntity(String encodingPassword) {
    return User.builder()
        .loginId(this.loginId)
        .password(encodingPassword)
        .realName(this.realName)
        .nickname(this.nickname)
        .profileImageUrl(this.profileImageUrl)
        .email(this.email)
        .phoneNumber(this.phoneNumber)
        .detailAddress(this.detailAddress)
        .zipcode(this.zipcode)
        .build();
  }


}
