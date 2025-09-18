package com.ecommerce.auth.service;

public interface OtpService {

    public String generateOtp(Long phoneNumber);

    public boolean verifyOtp(Long phoneNumber, String enteredOtp);

    public void sendOtpSms(Long phoneNumber, String otp);
}
