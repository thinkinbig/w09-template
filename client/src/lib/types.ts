export type Meal = {
    name: string;
    dish_type: string;
    labels: string[];
    favorite: boolean;
};

export type UserPreferences = {
    username: string;
    favoriteMeals: string[];
};

export type Recommendation = {
    recommendation: string;
};