package com.ssafy.jangan_backend.firelog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class FireLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int beaconId;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private byte isActiveFire;

    @Column(updatable = false)
    private Timestamp createdAt;
}
