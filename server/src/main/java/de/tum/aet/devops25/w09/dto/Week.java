package de.tum.aet.devops25.w09.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Week(int number, int year, List<Day> days) {
}
