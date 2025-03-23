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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beacon_a_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Beacon beaconA;

    @Column(name = "beacon_a_id")
    private Integer beaconAId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beacon_b_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Beacon beaconB;

    @Column(name = "beacon_b_id")
    private Integer beaconBId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}