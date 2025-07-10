package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.Category;

import java.util.List;

public class CreateRoadmapRequestDTO {
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
