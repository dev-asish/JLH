// API Configuration
// Uses VITE_API_BASE_URL environment variable if set, otherwise falls back to localhost
// To change API URL: Update .env file with VITE_API_BASE_URL and restart Vite dev server
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

