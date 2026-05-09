package com.zendr.backend.services.EmailAuthCode;

public interface EmailService {
    void sendAuthCode(String to, String code);
}