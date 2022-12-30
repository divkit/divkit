const path = require('path');
const fs = require('fs');
const {transform} = require('@babel/core');
const {VM} = require('vm2');

const tsBuilderSource = fs.readFileSync(
    path.resolve(__dirname, '../node_modules/@divkitframework/jsonbuilder/dist/jsonbuilder.js'),
    'utf-8'
);

function simpleHash(str) {
    let res = 0;
    for (let i = 0; i < str.length; ++i) {
        res = (res + str.charCodeAt(i) % 1029) % 1336337;
    }
    return res;
}

const tsBuilder = (() => {
    const exports = Object.create(null);
    const vm = new VM({
        allowAsync: false,
        sandbox: {
            require(name) {
                if (name === 'crypto') {
                    return {
                        createHash() {
                            return {
                                update(val) {
                                    return {
                                        digest() {
                                            return String(simpleHash(JSON.stringify(val)));
                                        }
                                    };
                                }
                            };
                        }
                    };
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
            exports: Object.create(null)
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
