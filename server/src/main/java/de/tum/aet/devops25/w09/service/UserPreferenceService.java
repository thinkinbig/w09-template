package de.tum.aet.devops25.w09.service;

import de.tum.aet.devops25.w09.UserPreferenceRepository;
import de.tum.aet.devops25.w09.entity.UserPreferences;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferenceService {

    private final UserPreferenceRepository repository;

    public UserPreferenceService(UserPreferenceRepository repository) {
        this.repository = repository;
    }

    public UserPreferences getPreferences(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return repository.findById(name).orElse(null);
    }

    public UserPreferences addPreferences(String name, String meal) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (meal == null || meal.trim().isEmpty()) {
            throw new IllegalArgumentException("Meal cannot be null or empty");
        }

        return repository.findById(name)
            .map(userPreferences -> {
                // Avoid duplicate meals
                if (!userPreferences.getFavoriteMeals().contains(meal)) {
                    userPreferences.getFavoriteMeals().add(meal);
                    return repository.save(userPreferences);
                }
                return userPreferences; // Return existing without saving if duplicate
            })
            .orElseGet(() -> {
                UserPreferences newPreferences = new UserPreferences(name, List.of(meal));
                return repository.save(newPreferences);
            });
    }

    public UserPreferences removePreference(String name, String meal) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (meal == null || meal.trim().isEmpty()) {
            throw new IllegalArgumentException("Meal cannot be null or empty");
        }

        return repository.findById(name)
            .map(userPreferences -> {
                if (userPreferences.getFavoriteMeals().contains(meal)) {
                    userPreferences.getFavoriteMeals().remove(meal);
                    return repository.save(userPreferences);
                }
                return userPreferences; // Return existing if meal not found
            })
            .orElse(null); // Return null if user preferences not found
    }
}

