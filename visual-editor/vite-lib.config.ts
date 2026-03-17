import { defineConfig, Plugin } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';
import { minify_sync as minify } from 'terser';

function minifyES(): Plugin {
    return {
        name: 'minifyES',
        generateBundle: {
            order: 'post',
            handler(_outputOptions, bundle) {
                for (const name in bundle) {
                    const chunk = bundle[name];
                    if (chunk.type === 'chunk') {
                        chunk.code = minify({
                            [chunk.name]: chunk.code,
                        }).code || '';
                    }
                }
            },
        }
    };
}

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
                    if (assetInfo.originalFileNames?.includes('style.css')) {
                        return 'divkit-editor.css';
                    }
                    return '[name].[ext]';
                },
            },
            plugins: [
                // dts()
                minifyES(),
            ]
        }
    }
});
