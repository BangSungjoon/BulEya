package com.ssafy.jangan_backend.common.config;

import java.io.InputStream;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {
	@PostConstruct
	public void init(){
		try{
			InputStream serviceAccount = new ClassPathResource("jangan204-firebase-adminsdk.json").getInputStream();
			FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
			if(FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
			}

		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
