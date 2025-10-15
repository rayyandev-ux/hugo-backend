package com.vocacional.prestamoinso.Service.impl;

import com.vocacional.prestamoinso.Entity.enums.ERole;
import com.vocacional.prestamoinso.Service.ClienteJpaService;
import com.vocacional.prestamoinso.Service.TrabajadorJpaService;
import com.vocacional.prestamoinso.Service.UserJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.vocacional.prestamoinso.Entity.User;
import java.util.Collection;
import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserJpaService userJpaService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userJpaService.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        
        User user = userOptional.get();
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRole())
        );
    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(ERole role) {
        return role == null ? null :
                java.util.Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }
}
