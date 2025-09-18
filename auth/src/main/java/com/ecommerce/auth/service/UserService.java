package com.ecommerce.auth.service;

import java.util.Map;

import com.ecommerce.auth.model.UserModel;

public interface UserService {
    public String userRegister(UserModel user);
    
    public Map<String, String> LoginUser(String identifier, String password);

    public UserModel getUser(String token);

    public Map<String, String> verifyOtp(long phoneNumber, String otp);
}
