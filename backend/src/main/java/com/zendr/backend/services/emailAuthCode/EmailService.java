package com.zendr.backend.services.emailAuthCode;

public interface EmailService {
    void sendAuthCode(String to, String code);
}