import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        svelte()
    ],
    build: {
        minify: true,
        lib: {
            entry: './src/lib.ts',
            formats: ['es']
        },
        rollupOptions: {
            output: {
                entryFileNames() {
                    return 'divkit-editor.js';
                },
                chunkFileNames(_chunkInfo) {
                    return '[name].js';
                },
                assetFileNames: assetInfo => {
                    if (assetInfo.name === 'style.css') {
                        return 'divkit-editor.css';
                    }
                    return '[name].[ext]';
                }
            },
            plugins: [
                // dts()
            ]
        }
    }
});
