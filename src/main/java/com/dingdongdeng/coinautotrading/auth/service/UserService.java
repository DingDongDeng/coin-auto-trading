package com.dingdongdeng.coinautotrading.auth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // fixme 고도화 필요
    String adminName = "admin";
    String password = new BCryptPasswordEncoder().encode("1234");
    if (!username.equals(adminName)) {
      throw new UsernameNotFoundException("User not authorized.");
    }
    return new User(username, password, List.of());
  }
}
