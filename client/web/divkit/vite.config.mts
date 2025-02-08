/* eslint-disable no-nested-ternary */

/// <reference types="vitest" />

import { defineConfig, type LibraryFormats, type Plugin, type PluginOption } from 'vite';
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

const BANNER_RE = /\/\*\!(?:.|\n)+?\*\//;

const moveEsbuildHelpersPlugin = (): Plugin => {
    return {
        name: 'move-esbuild-helpers-plugin',
        enforce: 'post',
        generateBundle(_, bundle) {
            // Dirty Vite fix
            // Vite produces correct iife code in lib mode,
            // only if exports are used and the lib.name doesn't contain a "."
            // This is almost the same code, as the original Vite code:
            // https://github.com/vitejs/vite/blob/a0ed4057c90a1135aa58d06305f446e232f63e2a/packages/vite/src/node/plugins/esbuild.ts#L347-L375
            for (const key in bundle) {
                const item = bundle[key];
                if (!key.endsWith('.js') || item.type !== 'chunk') {
                    continue;
                }
                const code = item.code;
                const contentIndex = code.search(/\(function\s*\(\)\s*{\s*['"]use strict['"];/);
                if (contentIndex >= 0) {
                    const helpers = code.slice(0, contentIndex);
                    const banner = (code.match(BANNER_RE) || [])[0] || '';
                    item.code = banner + code
                        .slice(contentIndex)
                        .replace('"use strict";', () => '"use strict";' + helpers)
                        .replace(BANNER_RE, '');
                }
            }
        }
    };
};

export default defineConfig(({ isSsrBuild, mode }) => {
    const isProd = mode === 'production';
    const outputSubDir = FORMAT === 'iife' ? 'browser/' : (FORMAT === 'es' ? 'esm/' : '');

    const plugins: PluginOption[] = [svelte({
        compilerOptions: {
            immutable: true,
            hydratable: HYDRATABLE
        }
    })];
    if (FORMAT === 'iife') {
        plugins.push(
            moveEsbuildHelpersPlugin()
        );
    }

    return {
        plugins,
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
            noExternal: ['clsx'],
        },
        build: {
            target: ['chrome67', 'safari14', 'firefox68'],
            emptyOutDir: false,
            sourcemap: true,
            lib: isSsrBuild ? {
                entry: 'src/server.ts',
                formats: [FORMAT]
            } : {
                entry: `src/client${DEVTOOL ? '-devtool' : ''}${FORMAT === 'iife' ? '-globals' : ''}`,
                formats: [FORMAT],
                name: 'unused'
            },
            minify: true,
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
