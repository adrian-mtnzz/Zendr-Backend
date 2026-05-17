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
                        <title>Código de verificación de Zendr</title>
                    </head>
                    <body style="margin:0;padding:0;background-color:#DFF0F7;font-family:Arial,Helvetica,sans-serif;color:#0E1116;">

                    <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                           style="background-color:#DFF0F7;padding:32px 16px;">
                        <tr>
                            <td align="center">

                                <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                                       style="max-width:560px;">

                                    <tr>
                                        <td align="center" style="padding-bottom:22px;">
                                            <div style="font-size:34px;font-weight:800;">
                                                <span style="color:#3FC6CA;">Zen</span>
                                                <span style="color:#4F8DE7;">dr</span>
                                            </div>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="background:#FFFFFF;
                                                   border:1px solid #D8E3EE;
                                                   border-radius:24px;
                                                   padding:36px 32px;">

                                            <h1 style="text-align:center;
                                                       font-size:26px;
                                                       margin-bottom:16px;">
                                                Verifica tu identidad
                                            </h1>

                                            <p style="text-align:center;
                                                      color:#8A9AB2;
                                                      margin-bottom:28px;">
                                                Introduce este código de 6 dígitos en Zendr.
                                            </p>

                                            <div style="text-align:center;margin-bottom:28px;">

                                                <div style="
                                                    display:inline-block;
                                                    background:linear-gradient(
                                                        135deg,
                                                        #45D3C1 0%%,
                                                        #489DDB 58%%,
                                                        #8A38F5 100%%
                                                    );
                                                    padding:3px;
                                                    border-radius:22px;
                                                ">

                                                    <div style="
                                                        background:#FFFFFF;
                                                        border-radius:19px;
                                                        padding:20px 32px;
                                                    ">

                                                        <span style="
                                                            font-family:'Courier New',monospace;
                                                            font-size:40px;
                                                            font-weight:800;
                                                            letter-spacing:12px;
                                                        ">
                                                            %s
                                                        </span>

                                                    </div>
                                                </div>
                                            </div>

                                            <p style="text-align:center;">
                                                Este código expira en
                                                <strong style="color:#4F8DE7;">
                                                    5 minutos
                                                </strong>.
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