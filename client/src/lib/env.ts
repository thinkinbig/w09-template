import { env } from '$env/dynamic/public';

export let BaseURL = env.PUBLIC_API_URL || "http://localhost:8080/api";