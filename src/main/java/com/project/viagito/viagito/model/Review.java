package com.project.viagito.viagito.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private double rating;
    @Column (columnDefinition = "TEXT")
    private String comment;
    @ManyToOne
    @JoinColumn (name = "user.id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn (name = "local.id", nullable = false)
    private Local local;
}
