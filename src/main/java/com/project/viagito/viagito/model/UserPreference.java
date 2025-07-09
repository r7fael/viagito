package com.project.viagito.viagito.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserPreference {
    @Id
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
    @Column(name = "max_distance_km", nullable = false)
    private int maxDistanceKm;
}
