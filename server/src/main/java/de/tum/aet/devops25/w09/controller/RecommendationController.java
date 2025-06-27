package de.tum.aet.devops25.w09.controller;

import de.tum.aet.devops25.w09.entity.UserPreferences;
import de.tum.aet.devops25.w09.dto.Dish;
import de.tum.aet.devops25.w09.service.CanteenService;
import de.tum.aet.devops25.w09.service.LLMRecommendationService;
import de.tum.aet.devops25.w09.service.UserPreferenceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RecommendationController {
    private final LLMRecommendationService llmRecommendationService;
    private final UserPreferenceService userPreferenceService;
    private final CanteenService canteenService;

    public RecommendationController(LLMRecommendationService llmRecommendationService, UserPreferenceService userPreferenceService, CanteenService canteenService) {
        this.llmRecommendationService = llmRecommendationService;
        this.userPreferenceService = userPreferenceService;
        this.canteenService = canteenService;
    }

    @GetMapping("/recommend/{name}")
    public ResponseEntity<Map<String, String>> getRecommendation(@PathVariable String name) {

        //Get the favorite meal from database
        UserPreferences userPreferences = userPreferenceService.getPreferences(name);

        if (userPreferences == null || userPreferences.getFavoriteMeals() == null || userPreferences.getFavoriteMeals().isEmpty()) {
            return ResponseEntity.noContent().build(); // No favorites found
        }

        //Get the today's menu from canteenService.getTodayMeals
        List<Dish> todaysMeals = canteenService.getTodayMeals("mensa-garching");

        // TODO call LLM service to get recommendation based on the user's favorites
        String responseFromLLMService = llmRecommendationService.getRecommendationFromLLM(userPreferences.getFavoriteMeals(), todaysMeals);

        if (responseFromLLMService.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(Map.of("recommendation", responseFromLLMService));
    }

    
}
