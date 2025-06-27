<script lang="ts">
    import {onMount} from "svelte";
    import "../app.css";

    import FoodCard from './FoodCard.svelte';
    import type {Meal} from '$lib/types';
    import type {PageProps} from "./$types";
    import { BaseURL } from '$lib/env';
    import { getCookie } from '$lib';

    let {data}: PageProps = $props();

    // For more information on runes and reactivity, see: https://svelte.dev/docs/svelte/what-are-runes
    let meals: Meal[] = $state(data.meals);
    let recommendation: string | undefined = data.recommendation?.recommendation;
</script>

<main>
    <header>
        <h1>Garching Campus Canteen</h1>
        <p>Today's menu offerings</p>
    </header>

    <!-- Recommendation Banner -->
    {#if recommendation}
        <div class="recommendation-banner">
            <div class="recommendation-content">
                <h3>🤖 AI Recommendation</h3>
                <p>{recommendation}</p>
            </div>
        </div>
    {:else}
        <div class="recommendation-banner empty">
            <div class="recommendation-content">
                <h3>🤖 AI Recommendation</h3>
                <p>No recommendations available. Try adding some favorite meals first!</p>
            </div>
        </div>
    {/if}

    {#if meals.length === 0}
        <div class="no-results">
            <p>Loading menu items...</p>
        </div>
    {:else}
        <div class="food-grid">
            {#each meals as {}, i}
                <FoodCard bind:meal={meals[i]}/>
            {/each}
        </div>
    {/if}

    {#if meals.length === 0 && meals.length > 0}
        <div class="no-results">
            No menu items match your filters. Try changing your selection.
        </div>
    {/if}
</main>
