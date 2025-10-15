package com.vocacional.prestamoinso.Service.impl;

import com.vocacional.prestamoinso.Entity.enums.ERole;
import com.vocacional.prestamoinso.Service.ClienteSupabaseService;
import com.vocacional.prestamoinso.Service.TrabajadorSupabaseService;
import com.vocacional.prestamoinso.Service.UserSupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.vocacional.prestamoinso.Entity.User;
import java.util.Collection;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ClienteSupabaseService clienteSupabaseService;
    @Autowired
    private TrabajadorSupabaseService trabajadorSupabaseService;
    @Autowired
    private UserSupabaseService userSupabaseService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User trabajador = userSupabaseService.findByUsernameWithPassword(username);
        if (trabajador == null) {
            throw new UsernameNotFoundException("Traabajador no encontrado: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                trabajador.getUsername(),
                trabajador.getPassword(),
                mapRolesToAuthorities(trabajador.getRole())
        );
    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(ERole role) {
        return role == null ? null :
                java.util.Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }
}
