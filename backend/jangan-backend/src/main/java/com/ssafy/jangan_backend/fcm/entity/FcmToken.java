package com.ssafy.jangan_backend.fcm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "fcm_token", indexes = @Index(name = "idx_uuid", columnList = "uuid"))
public class FcmToken {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "uuid", nullable = false)
	private String uuid;

	@Column(name = "token", nullable = false)
	private String token;

	public FcmToken(String uuid, String token){
		this.uuid = uuid;
		this.token = token;
	}
}
