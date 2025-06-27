package de.tum.aet.devops25.w09.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecommendRequest(
    @JsonProperty("favorite_menu") List<String> favoriteMenu,
    @JsonProperty("todays_menu") List<String> todaysMenu
) {}
