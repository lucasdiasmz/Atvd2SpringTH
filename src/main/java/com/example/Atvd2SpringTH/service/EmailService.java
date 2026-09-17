package com.example.Atvd2SpringTH.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject("Código para recuperação de senha");
        mensagem.setText(
                "Olá!\n\n" +
                "Seu código para recuperação de senha é: " + codigo + "\n\n" +
                "Se você não solicitou a alteração, ignore esta mensagem."
        );

        mailSender.send(mensagem);
    }
}
