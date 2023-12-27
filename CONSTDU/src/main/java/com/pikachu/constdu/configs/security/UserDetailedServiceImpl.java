package com.pikachu.constdu.configs.security;

import com.pikachu.constdu.models.Role;
import com.pikachu.constdu.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserDetailedServiceImpl implements UserDetailsService {

    @Autowired
    @Lazy
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        com.pikachu.constdu.models.User user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("Not Found");
            throw new UsernameNotFoundException("User " + email + "Not found");
        }

        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        Role role = user.getRole();
        grantList.add(new SimpleGrantedAuthority(role.getRoleName()));

        UserDetails userDetails = (UserDetails) new User(user.getEmail(), user.getPassword(), grantList);

        return userDetails;
    }
}
