import type { PageLoad } from './$types';
import { BaseURL} from '$lib/env';
import type { Meal, Recommendation, UserPreferences } from '$lib/types';
import { getCookie, setCookie } from '$lib';

export const ssr = false;

//get the username from the cookie
let username: string | null = null;

export const load: PageLoad = async ({ fetch }) => {
    username = getCookie('username');
    
    // Prompt for username if not found in cookie
    if (!username) {
        username = prompt('Please enter your username:');
        if (username) {
            setCookie('username', username, 30); // Store for 30 days
        } else {
            // Handle case where user cancels or enters empty string
            username = 'anonymous';
        }
    }

    // Execute all requests in parallel, don't wait for slow ones to complete
    const [mealsResult, preferencesResult, recommendationResult] = await Promise.allSettled([
        fetch(`${BaseURL}/mensa-garching/today`).then(res => res.json()),
        fetch(`${BaseURL}/preferences/${username}`).then(res => res.json()),
        fetch(`${BaseURL}/recommend/${username}`).then(res => res.json())
    ]);

    // Extract successful results or provide defaults
    const meals: Meal[] = mealsResult.status === 'fulfilled' ? mealsResult.value : [];
    const preferences: UserPreferences = preferencesResult.status === 'fulfilled' ? preferencesResult.value : { favoriteMeals: [] };
    const recommendation: Recommendation | null = recommendationResult.status === 'fulfilled' ? recommendationResult.value : null;

    console.log('Meals:', meals);
    console.log('Preferences:', preferences);
    console.log('Recommendation:', recommendation);

    // Set the boolean favorite property for each meal
    meals.forEach((meal: any) => {
        meal.favorite = preferences.favoriteMeals.includes(meal.name);
    });

    return { meals, recommendation };
};