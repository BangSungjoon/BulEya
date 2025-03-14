package com.ssafy.jangan_backend.edge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int beaconAId;

    @Column(nullable = false)
    private int beaconBId;

    @Column(nullable = false)
    private int distance;

    @Column(updatable = false)
    private Timestamp createdAt;
}
