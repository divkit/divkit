import { readFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

const __dirname = dirname(fileURLToPath(import.meta.url));

const {
    S3_PATH,
    VERSION = readFileSync(resolve(__dirname, '../../version'))
} = process.env;

export default defineConfig({
    plugins: [svelte()],
    define: {
        'process.env.VERSION': JSON.stringify(VERSION)
    },
    base: S3_PATH ? `https://yastatic.net/s3/${S3_PATH}/${VERSION}/` : '/',
    build: {
        rolldownOptions: {
            external: /(node:.+)|worker_threads|buffer/
        },
    },
    resolve: {
        alias: {
            '@divkit': resolve(__dirname, '../..')
        }
    }
});
