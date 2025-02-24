import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";

// const isProduction = process.env.NODE_ENV === 'production';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [svelte()],
  server: {
    proxy: {
      "/screen": {
        target: "https://172.19.83.21:8443",
        changeOrigin: true,
        secure: false,
      },
    },
    host: true,
    port: 5173,
  },
});
