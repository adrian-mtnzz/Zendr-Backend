package com.zendr.backend.config;

import com.zendr.backend.internal.user.model.User;
import com.zendr.backend.services.qr.QRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

@Component
public class UserQRListener extends AbstractMongoEventListener<User> {

    @Autowired
    private QRService qrService;

    @Override
    public void onBeforeSave(BeforeSaveEvent<User> event) {
        User user = event.getSource();
        org.bson.Document document = event.getDocument();

        if (user.getQRCode() == null && document != null) {
            System.out.println("Generando QR para: " + user.getEmail());
            try {
                String contenido = "Usuario: " + user.getEmail();
                String qrBase64 = qrService.generateQRAsBase64(contenido);

                user.setQRCode(qrBase64);
                document.put("QRCode", qrBase64);

                System.out.println("QR inyectado correctamente en el documento de MongoDB.");
            } catch (Exception e) {
                throw new RuntimeException("Error generando QR", e);
            }
        }
    }
}