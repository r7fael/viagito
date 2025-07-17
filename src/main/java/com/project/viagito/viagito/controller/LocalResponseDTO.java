package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.Local;

public class LocalResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private double latitude;
    private double longitude;
    private double averageRating;

    public LocalResponseDTO(Local local) {
        this.id = local.getId();
        this.name = local.getName();
        this.description = local.getDescription();
        this.category = local.getCategory().toString();
        this.latitude = local.getLatitude();
        this.longitude = local.getLongitude();

        if (local.getAverageRating() != null) {
            this.averageRating = local.getAverageRating();
        } else {
            this.averageRating = 0.0;
        }
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() {
        return category;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() { return longitude; }

    public double getAverageRating() {
        return averageRating;
    }
}