package de.tum.aet.devops25.w09.service;

import de.tum.aet.devops25.w09.client.LLMRestClient;
import de.tum.aet.devops25.w09.dto.Dish;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LLMRecommendationService {

    private final LLMRestClient llmRestClient;

    public LLMRecommendationService(LLMRestClient llmRestClient) {
        this.llmRestClient = llmRestClient;
    }

    /**
     * Get recommendation from LLM service using REST API
     * @param favoriteMeals list of user's favorite meal names
     * @param todayMeals list of today's available dishes
     * @return recommendation as a string
     */
    public String getRecommendationFromLLM(List<String> favoriteMeals, List<Dish> todayMeals) {
        try {
            // Convert today's dishes to meal names
            List<String> todayMealNames = todayMeals.stream()
                    .map(Dish::name)
                    .collect(Collectors.toList());

            // TODO Call REST service
            return llmRestClient.generateRecommendations(favoriteMeals, todayMealNames);

        } catch (Exception e) {
            System.err.println("Error fetching recommendation from LLM service: " + e.getMessage());
            return "";
        }
    }

}
