package com.mediFinder.security.services;

import com.mediFinder.models.User;
import com.mediFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.bytebuddy.utility.RandomString;

@Service
public class  UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;



  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

//    String randomCode = RandomString.make(64);
//    user.setVerification_code(randomCode);

    return UserDetailsImpl.build(user);
  }

}
