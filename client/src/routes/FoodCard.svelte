<script lang="ts">
    import Icon from "@iconify/svelte";
    import type {Meal} from '$lib/types';
    import { BaseURL } from "$lib/env";
    import { getCookie } from "$lib";

    let { meal = $bindable() }: { meal: Meal } = $props();    // List of favorite meal

    //toggle favorite when heart button is clicked
    const toggleFavorite = async () => {
        meal.favorite = !meal.favorite;

        try {
            const username = getCookie('username');
            if (!username) {
                console.error('No username found in cookie');
                meal.favorite = !meal.favorite; // Revert the change
                return;
            }

            const response = await fetch(`${BaseURL}/preferences/${encodeURIComponent(username)}?meal=${encodeURIComponent(meal.name)}`, {
                method: meal.favorite ? 'POST' : 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Failed to update favorite status');
            }
        } catch (error) {
            console.error('Error updating favorite status:', error);
            // Revert the change if the request fails
            meal.favorite = !meal.favorite;
        }
    };
</script>

<div class="food-card">
    <div class="card-header">
        <div>
            <h2>{meal.name}</h2>
            <p class="dish-type">{meal.dish_type}</p>
        </div>
        
        <button 
            class="like-button" 
            class:liked={meal.favorite}
            onclick={toggleFavorite}
            aria-label={meal.favorite ? "Unlike this meal" : "Like this meal"}
        >
            <Icon
                icon={meal.favorite ? "mdi:heart" : "mdi:heart-outline"}
                width="24"
                height="24"
            />
        </button>
    </div>

    <div class="label-section">
        {#if meal.labels.includes("VEGETARIAN")}
            <span class="label diet-label">
                <Icon
                        icon="mdi:leaf"
                        class="icon"
                        width="16"
                        height="16"
                />
                Vegetarian
            </span>
        {/if}

        {#if meal.labels.includes("VEGAN")}
            <span class="label diet-label">
                <Icon
                        icon="mdi:sprout"
                        class="icon"
                        width="16"
                        height="16"
                />
                Vegan
            </span>
        {/if}

        {#if meal.labels.includes("MEAT") || meal.labels.includes("PORK")}
            <span class="label meat-label">
                <Icon
                        icon="mdi:food-steak"
                        class="icon"
                        width="16"
                        height="16"
                />
                Meat
            </span>
        {/if}

        {#if meal.labels.includes("FISH")}
            <span class="label fish-label">
                <Icon
                        icon="mdi:fish"
                        class="icon"
                        width="16"
                        height="16"
                />
                Fish
            </span>
        {/if}
    </div>
</div>