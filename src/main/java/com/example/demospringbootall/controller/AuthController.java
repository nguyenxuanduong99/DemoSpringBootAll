package com.example.demospringbootall.controller;

import com.example.demospringbootall.common.ERole;
import com.example.demospringbootall.dto.JwtResponse;
import com.example.demospringbootall.dto.LoginRequest;
import com.example.demospringbootall.dto.MessageResponse;
import com.example.demospringbootall.dto.SignupRequest;
import com.example.demospringbootall.entities.Role;
import com.example.demospringbootall.entities.User;
import com.example.demospringbootall.repositories.RoleRepository;
import com.example.demospringbootall.repositories.UserRepository;
import com.example.demospringbootall.service.UserDetailsImpl;
import com.example.demospringbootall.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Qualifier(value = "duong")
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping(value = "/getAll")
    public @ResponseBody ResponseEntity<?> getAllUser(){
        List<User> list = userRepository.findAll();
        return ResponseEntity.ok(list);
    }
    @PostMapping(value = "/getacc")
    public @ResponseBody ResponseEntity<?> getAcc(@RequestHeader String Authorization){
        String username = jwtUtils.getUserNameFromJwtToken(Authorization);
        User user = userRepository.findByUsername(username);
        if(user!=null){
            return ResponseEntity.ok(user);
        }
//        User user = userRepository.findByUsername(loginRequest.getUsername());
//        String str = loginRequest.getPassword();
//        if(encoder.matches(str, user.getPassword())){
//            return ResponseEntity.ok(user);
//        }
        return ResponseEntity.ok("Loi");
    }

    @PostMapping(value = "/signin")
    public @ResponseBody ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
                        ,loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item->item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
    @PostMapping(value = "/signup")
    public @ResponseBody ResponseEntity<?> registerUser( @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already in use"));
        }
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use"));
        }
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                new BCryptPasswordEncoder().encode(signupRequest.getPassword()));

        Set<String> strRole = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRole == null){
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(()->new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        }else {
            strRole.forEach(role ->{
                switch (role){
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;
                    case "ROLE_MANAGER":
                        Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(managerRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User register successfully!"));
    }
}
