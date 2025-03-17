package com.ssafy.jangan_backend.edge.entity;

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
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer distance;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beaconAId", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Beacon beaconA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beaconBId", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Beacon beaconB;
}