package com.example.demospringbootall.service;

import com.example.demospringbootall.entities.User;
import com.example.demospringbootall.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

//    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user =userRepository.findByUsername(username) ;
            return UserDetailsImpl.build(user);
        }catch (Exception e){
            return null;
        }
    }
    public UserDetails loadUserEmail(String email){
        User user = userRepository.findAllByEmail(email);
        if(user != null){
            return UserDetailsImpl.build(user);
        }
        return null;
    }
}
