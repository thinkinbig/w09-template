package de.tum.aet.devops25.w09.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    @Id
    private String name;
    
    @ElementCollection
    private List<String> favoriteMeals;
    
    // Default constructor required by JPA
    public UserPreferences() {}
    
    // Constructor for convenience
    public UserPreferences(String name, List<String> favoriteMeals) {
        this.name = name;
        this.favoriteMeals = favoriteMeals;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getFavoriteMeals() {
        return favoriteMeals;
    }
}
