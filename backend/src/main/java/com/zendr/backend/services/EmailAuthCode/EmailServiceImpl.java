package com.zendr.backend.services.EmailAuthCode;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;
    
    @Override
    public void sendAuthCode(String to, String code) {
        
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Código de verificación");
        message.setText("""
                Tu código de verificación es:

                %s

                Este código expira en 5 minutos.
                """.formatted(code));
        
        mailSender.send(message);
    }
}