import { RollupOptions, Plugin } from 'rollup';
import typescript from '@rollup/plugin-typescript';
import dts from 'rollup-plugin-dts';

function emitModulePackageFile(): Plugin {
    return {
        generateBundle() {
            this.emitFile({ fileName: 'package.json', source: `{"type":"module"}`, type: 'asset' });
        },
        name: 'emit-module-package-file'
    };
}

export default (_command: Record<string, unknown>): RollupOptions | RollupOptions[] => {
    const cjs: RollupOptions = {
        input: {
            'jsonbuilder': 'src/index.ts'
        },
        output: {
            dir: 'dist',
            format: 'cjs',
            sourcemap: true,
            chunkFileNames: '[name]'
        },
        plugins: [
            typescript()
        ],
        external: [
            'crypto'
        ]
    };

    const esm: RollupOptions = {
        ...cjs,
        output: {
            ...cjs.output,
            dir: 'dist/es',
            format: 'es'
        },
        plugins: [
            typescript(),
            emitModulePackageFile()
        ]
    };

    const nodeTypings: RollupOptions = {
        input: 'src/index.ts',
        output: {
            file: 'dist/jsonbuilder.d.ts'
        },
        plugins: [
            dts()
        ]
    };

    return [
        cjs,
        esm,
        nodeTypings
    ];
};
