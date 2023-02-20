package com.develonity.admin.dto;

import com.develonity.user.entity.User;
import com.develonity.user.entity.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisterResponse {

  private final Long id;
  private final String loginId;
  private final String password;
  private final String realName;
  private final String nickName;
  private final String profileImageUrl;
  private final String email;
  private final String phoneNumber;
  private final User.Address address;
  private final UserRole userRole;

  public RegisterResponse(User user) {
    this.id = user.getId();
    this.loginId = user.getLoginId();
    this.password = user.getPassword();
    this.realName = user.getRealName();
    this.nickName = user.getNickname();
    this.profileImageUrl = user.getProfileImageUrl();
    this.email = user.getEmail();
    this.phoneNumber = user.getPhoneNumber();
    this.address = user.getAddress();
    this.userRole = user.getUserRole();
  }


}
