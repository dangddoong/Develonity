package com.develonity.user.service;

import com.develonity.common.jwt.JwtUtil;
import com.develonity.common.redis.RedisDao;
import com.develonity.user.dto.LoginRequest;
import com.develonity.user.dto.LoginResponse;
import com.develonity.user.dto.RegisterRequest;
import com.develonity.user.entity.User;
import com.develonity.user.repository.UserRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final RedisDao redisDao;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override
  @Transactional
  public void register(RegisterRequest registerRequest) {
    if (userRepository.existsByLoginId(registerRequest.getLoginId())) {
      throw new IllegalArgumentException("회원 중복");
    }
    String encodingPassword = passwordEncoder.encode(registerRequest.getPassword());
    User user = registerRequest.toEntity(encodingPassword);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public LoginResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByLoginId(loginRequest.getLoginId())
        .orElseThrow(IllegalArgumentException::new);
    if (user.isWithdrawal()) {
      throw new IllegalArgumentException("탈퇴한 회원입니다.");
    }
    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호 불일치");
    }
    if (user.isWithdrawal()) {
      throw new IllegalArgumentException("이미 탈퇴된 회원입니다.");
    }
    String accessToken = jwtUtil.createAccessToken(user.getLoginId(), user.getUserRole());
    String refreshToken = jwtUtil.createRefreshToken(user.getLoginId(), user.getUserRole());
    return new LoginResponse(accessToken, refreshToken);
  }

  @Override
  @Transactional
  public void logout(String refreshToken) {
    Long validMilliSeconds = jwtUtil.getValidMilliSeconds(refreshToken);
    redisDao.setValues(refreshToken, "", Duration.ofMillis(validMilliSeconds));
  }

  @Override
  @Transactional
  public void withdrawal(String refreshToken, String loginId, String password) {
    User user = userRepository.findByLoginId(loginId)
        .orElseThrow(IllegalArgumentException::new);
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new IllegalArgumentException("비밀번호 불일치");
    }
    user.withdraw();
    logout(refreshToken);
  }

}
