package com.ssafy.jangan_backend.beacon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beacon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer mapId;

    @Column(nullable = false)
    private Integer beaconCode;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Integer coordX;

    @Column(nullable = false)
    private Integer coordY;

    private Boolean isExit;

    private Boolean isCctv;

    private String cctvIp;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
