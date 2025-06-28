package com.project.viagito.viagito.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Id
@GeneratedValue
public class Local {
    public int id;
    public String name;
    public String description;
    public String category;
    public String latitude;
    public String longitude;
}
