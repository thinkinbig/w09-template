package de.tum.aet.devops25.w09;

import de.tum.aet.devops25.w09.client.LLMRestClient;
import de.tum.aet.devops25.w09.dto.Dish;
import de.tum.aet.devops25.w09.service.LLMRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LLMRecommendationServiceTest {

    @Mock
    private LLMRestClient llmRestClient;

    private LLMRecommendationService llmRecommendationService;

    @BeforeEach
    void setUp() {
        llmRecommendationService = new LLMRecommendationService(llmRestClient);
    }

    @Test
    void shouldGetRecommendationFromLLM() {
        // Given
        List<String> favoriteMeals = Arrays.asList("Pizza", "Pasta");
        List<Dish> todayMeals = Arrays.asList(
            new Dish("Margherita Pizza", "main", List.of("vegetarian")),
            new Dish("Chicken Curry", "main", List.of("spicy")),
            new Dish("Caesar Salad", "salad", List.of("fresh"))
        );
        
        String expectedRecommendation = "Margherita Pizza";
        
        when(llmRestClient.generateRecommendations(
            eq(favoriteMeals), 
            eq(Arrays.asList("Margherita Pizza", "Chicken Curry", "Caesar Salad"))
        )).thenReturn(expectedRecommendation);

        // When
        String result = llmRecommendationService.getRecommendationFromLLM(favoriteMeals, todayMeals);

        // Then
        assertThat(result).isEqualTo(expectedRecommendation);
    }

    @Test
    void shouldReturnEmptyStringWhenExceptionOccurs() {
        // Given
        List<String> favoriteMeals = List.of("Pizza");
        List<Dish> todayMeals = List.of(
                new Dish("Margherita Pizza", "main", List.of("vegetarian"))
        );
        
        when(llmRestClient.generateRecommendations(any(), any()))
            .thenThrow(new RuntimeException("Service unavailable"));

        // When
        String result = llmRecommendationService.getRecommendationFromLLM(favoriteMeals, todayMeals);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleEmptyMealLists() {
        // Given
        List<String> favoriteMeals = List.of();
        List<Dish> todayMeals = List.of();
        
        when(llmRestClient.generateRecommendations(eq(favoriteMeals), eq(List.of())))
            .thenReturn("");

        // When
        String result = llmRecommendationService.getRecommendationFromLLM(favoriteMeals, todayMeals);

        // Then
        assertThat(result).isEmpty();
    }
}
