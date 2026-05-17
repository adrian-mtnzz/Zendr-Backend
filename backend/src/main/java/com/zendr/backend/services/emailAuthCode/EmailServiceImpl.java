package com.zendr.backend.services.emailAuthCode;

import com.zendr.backend.services.emailAuthCode.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;
    
    @Override
    public void sendAuthCode(String to, String code) {
        
        try {
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Código de verificación");
            
            String html = """
             <!doctype html>
             <html lang="es">
             <head>
                 <meta charset="utf-8">
                 <meta name="viewport" content="width=device-width, initial-scale=1">
                 <meta http-equiv="x-ua-compatible" content="ie=edge">
                 <title>Código de verificación de Zendr</title>
    
                 <style>
                     * {
                         box-sizing: border-box;
                     }
    
                     @media only screen and (max-width: 600px) {
                         .email-shell {
                             padding: 24px 12px !important;
                         }
    
                         .container {
                             width: 100%% !important;
                             max-width: 100%% !important;
                         }
    
                         .card {
                             padding: 28px 20px !important;
                         }
    
                         .code {
                             font-size: 32px !important;
                             letter-spacing: 8px !important;
                         }
                     }
    
                     @media only screen and (max-width: 360px) {
                         .code {
                             font-size: 28px !important;
                             letter-spacing: 6px !important;
                         }
                     }
                 </style>
             </head>
    
             <body style="margin: 0; padding: 0; background-color: #DFF0F7; font-family: Arial, Helvetica, sans-serif; color: #0E1116;">
    
                 <div style="display: none; max-height: 0; overflow: hidden; opacity: 0; color: transparent;">
                     Tu código de verificación de Zendr expira en 5 minutos.
                 </div>
    
                 <table role="presentation"
                        class="email-shell"
                        width="100%%"
                        cellspacing="0"
                        cellpadding="0"
                        border="0"
                        style="width: 100%%; background-color: #DFF0F7; margin: 0; padding: 32px 16px;">
    
                     <tr>
                         <td align="center">
    
                             <table role="presentation"
                                    class="container"
                                    width="100%%"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                    style="width: 100%%; max-width: 560px; margin: 0 auto;">
    
                                 <tr>
                                     <td align="center" style="padding: 0 0 22px;">
                                         <div style="font-size: 34px; line-height: 1; font-weight: 800; color: #0E1116;">
                                             <span style="color: #3FC6CA;">Zen</span>
                                             <span style="color: #4F8DE7;">dr</span>
                                         </div>
                                     </td>
                                 </tr>
    
                                 <tr>
                                     <td class="card"
                                         style="background-color: #FFFFFF;
                                                border: 1px solid #D8E3EE;
                                                border-radius: 24px;
                                                padding: 36px 32px;
                                                box-shadow: 0 16px 40px rgba(94, 168, 241, 0.18);">
    
                                         <table role="presentation"
                                                width="100%%"
                                                cellspacing="0"
                                                cellpadding="0"
                                                border="0">
    
                                             <tr>
                                                 <td align="center" style="padding: 0 0 18px;">
                                                     <div style="width: 64px;
                                                                 height: 64px;
                                                                 border-radius: 50%%;
                                                                 background-color: #DFF0F7;
                                                                 border: 1px solid #D8E3EE;
                                                                 text-align: center;
                                                                 line-height: 64px;">
    
                                                         <span style="font-size: 30px; color: #4F8DE7;">
                                                             &#10003;
                                                         </span>
                                                     </div>
                                                 </td>
                                             </tr>
    
                                             <tr>
                                                 <td align="center" style="padding: 0 0 10px;">
                                                     <h1 style="margin: 0;
                                                                font-size: 26px;
                                                                line-height: 34px;
                                                                font-weight: 800;
                                                                color: #0E1116;">
                                                         Verifica tu identidad
                                                     </h1>
                                                 </td>
                                             </tr>
    
                                             <tr>
                                                 <td align="center" style="padding: 0 0 28px;">
                                                     <p style="margin: 0;
                                                               font-size: 16px;
                                                               line-height: 24px;
                                                               color: #8A9AB2;">
    
                                                         Introduce este código de 6 dígitos en Zendr para continuar con tu solicitud.
                                                     </p>
                                                 </td>
                                             </tr>
    
                                             <tr>
                                                 <td align="center" style="padding: 0 0 28px;">
    
                                                     <div style="width: 100%%;
                                                                 max-width: 360px;
                                                                 margin: 0 auto;
                                                                 background: linear-gradient(
                                                                     135deg,
                                                                     #45D3C1 0%%,
                                                                     #489DDB 58%%,
                                                                     #8A38F5 100%%
                                                                 );
                                                                 border-radius: 22px;
                                                                 padding: 3px;">
    
                                                         <div style="width: 100%%;
                                                                     background-color: #FFFFFF;
                                                                     border-radius: 19px;
                                                                     padding: 20px 16px;">
    
                                                             <div class="code"
                                                                  style="font-family: 'Courier New', Courier, monospace;
                                                                         font-size: 40px;
                                                                         line-height: 48px;
                                                                         font-weight: 800;
                                                                         letter-spacing: 12px;
                                                                         color: #0E1116;
                                                                         white-space: nowrap;">
    
                                                                 %s
                                                             </div>
                                                         </div>
                                                     </div>
                                                 </td>
                                             </tr>
    
                                             <tr>
                                                 <td align="center" style="padding: 0 0 22px;">
                                                     <p style="margin: 0;
                                                               font-size: 15px;
                                                               line-height: 23px;
                                                               color: #0E1116;">
    
                                                         Este código expira en
                                                         <strong style="color: #4F8DE7;">
                                                             5 minutos
                                                         </strong>.
                                                     </p>
                                                 </td>
                                             </tr>
    
                                             <tr>
                                                 <td style="padding: 18px 18px;
                                                            background-color: #DFF0F7;
                                                            border-radius: 16px;">
    
                                                     <p style="margin: 0;
                                                               font-size: 13px;
                                                               line-height: 20px;
                                                               color: #8A9AB2;
                                                               text-align: center;">
    
                                                         Si no has solicitado este código,
                                                         puedes ignorar este correo.
                                                         Por seguridad, no compartas este código con nadie.
                                                     </p>
                                                 </td>
                                             </tr>
    
                                         </table>
                                     </td>
                                 </tr>
    
                                 <tr>
                                     <td align="center" style="padding: 22px 12px 0;">
    
                                         <p style="margin: 0;
                                                   font-size: 12px;
                                                   line-height: 18px;
                                                   color: #8A9AB2;">
    
                                             © Zendr. Este mensaje se ha enviado automáticamente.
                                         </p>
    
                                     </td>
                                 </tr>
    
                             </table>
    
                         </td>
                     </tr>
                 </table>
    
             </body>
             </html>
        """.formatted(code);
            
            helper.setText(html, true);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }
}