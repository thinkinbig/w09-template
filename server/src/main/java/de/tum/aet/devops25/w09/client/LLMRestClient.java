package de.tum.aet.devops25.w09.client;

import de.tum.aet.devops25.w09.dto.RecommendRequest;
import de.tum.aet.devops25.w09.dto.RecommendResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class LLMRestClient {

    private final RestClient restClient;

    public LLMRestClient(RestClient.Builder builder, @Value("${llm.service.url:http://localhost:5000}") String llmServiceUrl) {
        this.restClient = builder
                .baseUrl(llmServiceUrl)
                .build();
    }

    /**
     * Generate recommendations using the REST LLM service
     * @param favoriteMenu list of user's favorite meal names
     * @param todaysMenu list of today's available meal names
     * @return recommendation string
     */
    public String generateRecommendations(List<String> favoriteMenu, List<String> todaysMenu) {
        try {
            // TODO Create request body
            RecommendRequest request = new RecommendRequest(favoriteMenu, todaysMenu);

            // TODO Make REST call
            RecommendResponse response = restClient.post()
                    .uri("/recommend")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(RecommendResponse.class);

            // TODO Extract the recommendation
            return response != null ? response.recommendation() : "";

        } catch (Exception e) {
            System.err.println("Error calling LLM REST service: " + e.getMessage());
            return "";
        }
    }
}
