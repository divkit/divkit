import type { RollupOptions } from 'rollup';
import dts from 'rollup-plugin-dts';

export default (): RollupOptions | RollupOptions[] => {
    const typings: RollupOptions = {
        input: 'src/lib.ts',
        output: {
            file: 'dist/divkit-editor.d.ts'
        },
        plugins: [
            dts()
        ]
    };

    return [
        typings
    ];
};
