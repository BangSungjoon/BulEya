package com.ssafy.jangan_backend.beacon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Beacon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int mapId;

    @Column(nullable = false)
    private int beaconCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int coordX;

    @Column(nullable = false)
    private int coordY;

    @Column
    private Boolean isExit;

    @Column
    private Boolean isCctv;

    @Column
    private String cctvIp;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
