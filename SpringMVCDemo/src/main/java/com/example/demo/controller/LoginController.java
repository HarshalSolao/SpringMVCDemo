package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.utility.GoogleAuthUtility;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	GoogleAuthUtility googleAuthUtility;
	
	@GetMapping("/otpform")
	public String getOTPFormPage() {
		return "otpform";
	}
	
	@PostMapping("/otp")
	public String checkOTP(@RequestParam("otp") int otp) {
		System.out.println(otp);
		//String url  = googleAuthUtility.getSecretKeyUrl("harshalsolao@gmail.com");
		String url = "https://chart.googleapis.com/chart?chs=200x200&chld=M%7C0&cht=qr&chl=otpauth%3A%2F%2Ftotp%2FAadhar%2520Card%3Aharshalsolao%40gmail.com%3Fsecret%3D5EVDF5PSM4ZLGXXF%26issuer%3DAadhar%2BCard%26algorithm%3DSHA1%26digits%3D6%26period%3D30";
		
		String secretKey = googleAuthUtility.getKeyFromUrl(url);
		System.out.println(secretKey);
		System.out.println();
		
		boolean flag = googleAuthUtility.authenticated(secretKey, otp);
		if(flag) {
			return "success";
		} else {
			return "otpform";
		}
		
	}

}
