/* eslint-disable no-nested-ternary */

/// <reference types="vitest" />

import { defineConfig, type LibraryFormats } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';
import pkg from './package.json';

const HYDRATABLE = Boolean(process.env.HYDRATABLE);
const DEVTOOL = Boolean(process.env.DEVTOOL);
const FORMAT = (process.env.FORMAT || 'cjs') as LibraryFormats;

const banner = `/*!
    DivKit v${pkg.version}
	https://github.com/divkit/divkit
	@licence Apache-2.0
*/`;

export default defineConfig(({ isSsrBuild, mode }) => {
    const isProd = mode === 'production';
    const outputSubDir = FORMAT === 'iife' ? 'browser/' : (FORMAT === 'es' ? 'esm/' : '');

    return {
        plugins: [svelte()],
        define: {
            'process.env.DEVTOOL': JSON.stringify(DEVTOOL),
            'process.env.IS_PROD': JSON.stringify(isProd),
            'process.env.ENABLE_EXPRESSIONS': JSON.stringify(true)
        },
        css: {
            modules: {
                generateScopedName: isProd ? 'divkit-[hash:5]' : undefined
            }
        },
        ssr: {
            noExternal: ['clsx']
        },
        build: {
            target: ['chrome67', 'safari14', 'firefox68'],
            emptyOutDir: false,
            sourcemap: true,
            lib: isSsrBuild ? {
                entry: 'src/server.ts',
                formats: [FORMAT]
            } : {
                entry: DEVTOOL ? 'src/client-devtool' : 'src/client.ts',
                formats: [FORMAT],
                name: 'Ya.Divkit'
            },
            minify: FORMAT === 'iife',
            rollupOptions: {
                external: [],
                output: {
                    banner,
                    entryFileNames() {
                        return outputSubDir + (isSsrBuild ?
                            `server.${FORMAT === 'es' ? 'm' : ''}js` :
                            `client${DEVTOOL ? '-devtool' : (HYDRATABLE ? '-hydratable' : '')}.${FORMAT === 'es' ? 'm' : ''}js`
                        );
                    },
                    assetFileNames() {
                        return 'client.[ext]';
                    },
                }
            }
        },
        test: {
            include: ['tests/**/*.test.ts'],
            reporters: process.env.CI ? ['html', 'default'] : ['default'],
            outputFile: 'ci/vitest-report/index.html',
        }
    };
});
