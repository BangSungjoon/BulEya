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
    @JoinColumn(name = "map_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Map map;
    @Column(name = "map_id", nullable = false)
    private Integer mapId;

    @Column(name = "beacon_code", nullable = false)
    private Integer beaconCode;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "coord_x" , nullable = false)
    private Double coordX;

    @Column(name = "coord_y", nullable = false)
    private Double coordY;

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