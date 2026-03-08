package com.zendr.backend.config;

import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.services.QRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class UserQRListener extends AbstractMongoEventListener<User> {

    @Autowired
    private QRService qrService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<User> event) {
        User user = event.getSource();

        // El QR solo se genera si no existe ya uno para ese usuario
        if (user.getQRCode() == null) {
            try {
                // El QR se genera usando su email
                String contenido = "Usuario: " + user.getEmail();
                user.setQRCode(qrService.generateQRAsBase64(contenido));
            } catch (Exception e) {
                throw new RuntimeException("Error generando QR", e);
            }
        }
    }
}