package com.example.demo.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

@Component
public class GoogleAuthUtility {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private String issuer = "Aadhar Card";

	GoogleAuthenticator gAuth = new GoogleAuthenticator();

	public String getSecretKeyUrl(String accountName) {
		String url = null;
		final GoogleAuthenticatorKey key = gAuth.createCredentials();
		url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, accountName, key);
		return url;
	}

	public String getKeyFromUrl(String keyUrl) {
		String key = null;
		String arr[] = keyUrl.split("3Fsecret");
		key = arr[1].substring(3, 19);
		return key;
	}

	public boolean authenticated(String secretKey, int otp) {
		return gAuth.authorize(secretKey, otp);
	}

}
