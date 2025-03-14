package com.ssafy.jangan_backend.beacon.entity;

import com.ssafy.jangan_backend.map.entity.Map;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapId", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Map map;

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

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}