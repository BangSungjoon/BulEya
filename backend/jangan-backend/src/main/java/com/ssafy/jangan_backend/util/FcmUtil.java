package com.ssafy.jangan_backend.util;

import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Component
public class FcmUtil {
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * alert Topic을 구독한 모든 사용자에게 푸시 알림을 보낸다.
	 * @param title
	 * @param body
	 * @return -1 :전송 실패, 0 : 전송 성공
	 */
	public int sendMessage(String title, String body) {
		try{
			String message = FirebaseMessaging.getInstance().send(Message.builder()
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body).build())
				.setTopic("alert")
				.build());

		} catch(Exception e){
			return -1;
		}
		return 0;
	}

	/**
	 * alert Topic을 구독한 모든 사용자에게 푸시 데이터를 보낸다.
	 * @param dto
	 * @return -1 : 전송 실패, 0 : 전송 성공
	 */
	public int sendMessage(Object dto) {
		try{
			String jsonString = objectMapper.writeValueAsString(dto);
			ApiFuture<String> messageIdFuture = FirebaseMessaging.getInstance().sendAsync(
				Message.builder()
					.putData("payload", jsonString)
					.setTopic("alert")
					.build()
			);
			messageIdFuture.addListener(() -> {
				try{
					String messageId = messageIdFuture.get();
				}catch(Exception e){

				}
			}, Executors.newSingleThreadExecutor());
		} catch(Exception e){
			return -1;
		}
		return 0;
	}
}
