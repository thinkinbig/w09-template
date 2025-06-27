package de.tum.aet.devops25.w09.service;

import de.tum.aet.devops25.w09.dto.Day;
import de.tum.aet.devops25.w09.dto.Dish;
import de.tum.aet.devops25.w09.dto.Week;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CanteenService {

    private final RestClient restClient;
    private final Clock clock;

    public CanteenService(RestClient.Builder builder, Clock clock,
                          @Value("${canteen.api.base-url:https://tum-dev.github.io/eat-api/}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.clock = clock;
    }

    /**
     * Get today's meals for a specific canteen
     * @param canteenName the name of the canteen, e.g., "mensa-garching"
     * @return list of dishes available today, or empty list if no data found
     */
    public List<Dish> getTodayMeals(String canteenName) {
        LocalDate today = LocalDate.now(clock);
        int year = today.getYear();
        int weekNumber = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        String weekStr = String.format("%02d", weekNumber);
        
        try {
            Week week = restClient.get()
                    .uri(canteenName + "/" + year + "/" + weekStr + ".json")
                    .retrieve()
                    .body(Week.class);
            
            if (week != null && week.days() != null) {
                Optional<Day> todayMenu = week.days().stream()
                        .filter(day -> day.date().equals(today))
                        .findFirst();
                
                return todayMenu.map(Day::dishes).orElse(List.of());
            }
        } catch (Exception e) {
            // Log the exception or handle it as appropriate
            System.err.println("Error fetching meals: " + e.getMessage());
        }
        
        return List.of();
    }
}
