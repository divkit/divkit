const crypto = require('crypto');
const path = require('path');
const fs = require('fs');
const {transform} = require('@babel/core');
const {VM} = require('vm2');

const tsBuilderSource = fs.readFileSync(
    path.resolve(__dirname, '../node_modules/@divkit/jsonbuilder-internal-test/dist/jsonbuilder.js'),
    'utf-8'
);

const tsBuilder = (() => {
    const exports = {};
    const vm = new VM({
        allowAsync: false,
        sandbox: {
            require(name) {
                if (name === 'crypto') {
                    return crypto;
                }
                throw new Error('Module is not found');
            },
            exports
        }
    });

    vm.run(tsBuilderSource);

    return exports;
})();

const RUN_TIMEOUT = 150;

function runTask(task) {
    const code = task.code;

    const result = transform(code, {
        // filename: 'test.ts',
        presets: [
            [
                require('@babel/preset-env')
            ]
        ],
        sourceMaps: 'inline',
        plugins: [
            [require('@babel/plugin-transform-typescript'), {
                onlyRemoveTypeImports: true
            }]
        ],
        highlightCode: false
    });

    const vm = new VM({
        timeout: RUN_TIMEOUT,
        allowAsync: false,
        sandbox: {
            require(name) {
                if (name === '@divkitframework/jsonbuilder') {
                    return tsBuilder;
                }
                throw new Error('Module is not found');
            },
            exports: {}
        }
    });

    vm.run(result.code);

    return vm.run('exports.getJson()');
}

process.on('message', task => {
    try {
        const result = runTask(task);
        process.send({
            ok: true,
            id: task.id,
            result
        });
    } catch (err) {
        console.log(err);
        process.send({
            ok: false,
            id: task.id,
            // error: err && typeof err.message === 'string' ? err.message : 'Failed to run the code'
            error: 'Failed to run the code'
        });
    }
});
