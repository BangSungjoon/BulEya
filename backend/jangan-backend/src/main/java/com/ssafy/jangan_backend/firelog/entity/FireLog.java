package com.ssafy.jangan_backend.firelog.entity;

import com.ssafy.jangan_backend.beacon.entity.Beacon;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FireLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean isActiveFire;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beaconId", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    private Beacon beacon;
}
