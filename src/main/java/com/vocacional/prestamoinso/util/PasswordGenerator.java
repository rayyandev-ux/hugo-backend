package com.vocacional.prestamoinso.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String encodedPassword = encoder.encode(password);
        System.out.println("Contraseña original: " + password);
        System.out.println("Contraseña encriptada: " + encodedPassword);
    }
}