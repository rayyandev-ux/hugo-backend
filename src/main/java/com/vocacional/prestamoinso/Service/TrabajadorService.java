package com.vocacional.prestamoinso.Service;


import com.vocacional.prestamoinso.Entity.Trabajador;
import com.vocacional.prestamoinso.Entity.User;
import com.vocacional.prestamoinso.Service.TrabajadorJpaService;
import com.vocacional.prestamoinso.Service.UserJpaService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
public class TrabajadorService {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TrabajadorJpaService trabajadorJpaService;
    @Autowired
    private UserJpaService userJpaService;
    @Autowired
    private JavaMailSenderImpl mailSender;


    public void deleteUser(Long id) throws Exception {
        Trabajador trabajador = trabajadorJpaService.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + id));

        trabajadorJpaService.delete(trabajador);
    }


    public void generateResetPasswordToken(String email) throws Exception {
        Optional<User> userOpt = userJpaService.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new Exception("No se encontró un usuario con ese correo.");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userJpaService.save(user);

        sendResetPasswordEmail(user.getEmail(), token);
    }

    public void resetPassword(String token, String newPassword) throws Exception {
        Optional<Trabajador> optionalTrabajador = trabajadorJpaService.findByResetPasswordToken(token);
        if (optionalTrabajador.isPresent()) {
            Trabajador trabajador = optionalTrabajador.get();
            trabajador.setPassword(passwordEncoder.encode(newPassword));
            trabajador.setResetPasswordToken(null);
            trabajador.setNeedsPasswordChange(false);
            trabajadorJpaService.save(trabajador);
        } else {
            throw new Exception("Token de recuperación inválido o expirado");
        }
    }

    public java.util.List<Trabajador> listarTodos() {
        return trabajadorJpaService.findAll();
    }

    private void sendResetPasswordEmail(String email, String token) throws Exception {
        String resetLink = "https://insofinal-frontend.vercel.app/reset-password?token=" + token;
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(email);
        helper.setSubject("Restablecer tu contraseña en PrestamosAPP");
        helper.setText("<p>Hola,</p>" +
                "<p>Esperamos que estés teniendo un buen día. Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en PrestamosAPP.</p>" +
                "<p>Si deseas restablecer tu contraseña, haz clic en el enlace de abajo:</p>" +
                "<a href=\"" + resetLink + "\">Restablecer contraseña</a>" +
                "<p>Si no solicitaste este cambio, puedes ignorar este correo.</p>" +
                "<p>Atentamente,<br>El equipo de desarrollo de PrestamosAPP</p>"+
                "<p>Tener en cuenta que el link caduce dentro de 2 horas</p>", true);
        mailSender.send(message);
    }
}
