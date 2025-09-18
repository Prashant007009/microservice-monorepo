package com.ecommerce.auth.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.ecommerce.auth.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.model.UserModel;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.auth.service.UserService;
import com.ecommerce.auth.utility.JwtUtil;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;
    private final OtpService otpService;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwt, OtpService otpService){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
        this.otpService = otpService;
    }

    @Override
    public String userRegister(UserModel user) {
        UserModel existingUserByEmail = userRepo.findByEmail(user.getEmail()).orElse(null);
        UserModel existingUserByPhone = userRepo.findByPhoneNumber(user.getPhoneNumber()).orElse(null);
        if(existingUserByEmail != null || existingUserByPhone != null){
            throw new RuntimeException("User Already Exists");
        }
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
        otpService.generateOtp(user.getPhoneNumber());
        userRepo.save(user);
        return "Otp sent to mobile number " + user.getPhoneNumber() + " Pending Verification";
    }

    @Override
    public UserModel getUser(String token) {
        boolean isTokenValid = jwt.validateToken(token);
        System.out.println("isTokenValid: " + isTokenValid);
		if (isTokenValid) {
			String email = jwt.extractUsername(token);
			return userRepo.findByEmail(email).orElse(null);
		}else {
			return null;
		}
    }

    @Override
    public Map<String, String> verifyOtp(long phoneNumber, String otp) {
        Map<String, String> response = new HashMap<>();
        UserModel user = userRepo.findByPhoneNumber(phoneNumber).orElse(null);
        if(user == null) {
        	throw new RuntimeException("User Not Found");
        }
        boolean isVerified = user.isVerified();
        if(isVerified) {
        	response.put("message", "User Already Verified");
        	return response;
        }
        boolean isOtpValid = otpService.verifyOtp(phoneNumber, otp);
        if(isOtpValid) {
        	user.setVerified(true);
        	userRepo.save(user);
        	response.put("message", "User Verified Successfully");
        }else {
        	response.put("message", "Invalid Otp");
        }
        return response;
    }

    @Override
	public Map<String, String> LoginUser(String identifier, String password){
        Optional<UserModel> userOpt;
        Map<String, String> response = new HashMap<>();
		try{
            long phoneNumber = Long.parseLong(identifier);
            userOpt = userRepo.findByPhoneNumber(phoneNumber);
            System.out.println("userOpt phone : " + userOpt);
        }catch(NumberFormatException e){
            userOpt = userRepo.findByEmail(identifier);
            System.out.println("userOpt email : " + userOpt);
        }
        UserModel user = userOpt.orElse(null);
        boolean isVerified = user != null && user.isVerified();
        if(user != null && !isVerified) {
            response.put("message", "Invalid Credentials");
        	throw new RuntimeException("User Not Verified");
        }
        System.out.println("user: " + user);
        String email = user != null ? user.getEmail() : null;
        long phoneNumber = user != null ? user.getPhoneNumber() : 0;
		System.out.printf("email: " + email + " password: " + password, user);

		if(user != null && passwordEncoder.matches(password, user.getPassword())) {
			String accessToken = jwt.generateAccessToken(email, phoneNumber);
			String refreshToken = jwt.generateRefreshToken(email, phoneNumber);
			response.put("accessToken", accessToken);
			response.put("refreshToken", refreshToken);
		}else {
	        response.put("message", "Invalid Credentials");
	    }
		return response;
	}
    
}
