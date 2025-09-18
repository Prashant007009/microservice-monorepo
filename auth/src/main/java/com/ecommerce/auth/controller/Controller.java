package com.ecommerce.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.auth.model.UserModel;
import com.ecommerce.auth.service.UserService;


@RestController
@RequestMapping("/api/v1/auth")
public class Controller {

    private final UserService userService;

    @Autowired
    public Controller(UserService userService){
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserModel user){
        try{
            String userRes = userService.userRegister(user);
            return ResponseEntity.ok(userRes);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> otpData) {
        try {
            long phoneNumber = Long.parseLong(otpData.get("phoneNumber"));
            String otp = otpData.get("otp");
            Map<String, String> verifyRes = userService.verifyOtp(phoneNumber, otp);
            return ResponseEntity.ok(verifyRes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Something Went Wrong!!");
        }
    }
    
    @PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
		try {
			String identifier = loginData.get("identifier");
			String password = loginData.get("password");
			Map<String, String> loginRes = userService.LoginUser(identifier, password);
			return ResponseEntity.ok(loginRes);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Something Went Wrong!!");
		}
	}

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authHeader){
        try{
        	String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            UserModel userRes = userService.getUser(token);
            return ResponseEntity.ok(userRes);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Something Went Wrong!!");
        }
    }
}
