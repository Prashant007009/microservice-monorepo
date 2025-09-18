package com.ecommerce.auth.service.impl;

import com.ecommerce.auth.service.OtpService;
import com.ecommerce.auth.utility.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {

    private static class OtpData {
        String otp;
        LocalDateTime expiry;

        OtpData(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }

    private final ConcurrentHashMap<Object, Object> otpStorage = new ConcurrentHashMap<>();
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final TwilioConfig twilioConfig;

    public OtpServiceImpl(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    @Override
    public String generateOtp(Long phoneNumber) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStorage.put(phoneNumber, new OtpData(otp, expiry));
        sendOtpSms(phoneNumber, otp);
        return otp;
    }

    @Override
    public boolean verifyOtp(Long phoneNumber, String enteredOtp) {
        OtpData otpData = (OtpData) otpStorage.get(phoneNumber);
        if (otpData != null && otpData.otp.equals(enteredOtp) && LocalDateTime.now().isBefore(otpData.expiry)) {
            otpStorage.remove(phoneNumber);
            return true;
        }
        return false;
    }

    @Override
    public void sendOtpSms(Long phoneNumber, String otp) {
        String message = "Your OTP is: " + otp + ". It is valid for " + OTP_EXPIRY_MINUTES + " minutes.";
        Message.creator(
                new com.twilio.type.PhoneNumber("+91" + phoneNumber),
                new com.twilio.type.PhoneNumber(twilioConfig.getFromNumber()),
                message
        ).create();
    }
}
